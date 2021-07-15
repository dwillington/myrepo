import groovy.json.*

def call(body) {
	ansiColor('xterm') {
		// evaluate the body block, and collect configuration into the object
		def pipelineParams = [:]
		body.delegate = pipelineParams
		body.resolveStrategy = Closure.DELEGATE_FIRST
		body()

		Map config = null // stores entire job configuration
		Map workflow = null // stores steps for current branch 
		Map defaultWorkflow = null // stores steps for the default branch

		String scmUrl;
		String branchPrefix; // this will only capture the branch name prefix i.e feature/abc -> branchPrefix = feature
		String fullBranchName;

		if(env.BRANCH_NAME) { 
			// MULTI BRANCH PIPELINE, scm url, branch parameters are introspected
			// scmUrl = this.scm.getSource()
			scmUrl = this.scm.userRemoteConfigs[0].url
			fullBranchName = env.BRANCH_NAME
		}
		else {
			// REGULAR PIPELINE, scm url, branch parameters are passed in
			scmUrl = pipelineParams.scmUrl
			fullBranchName = pipelineParams.branch
		}

		branchPrefix = fullBranchName.split('/')[0]

		// if (env.BRANCH_NAME.split('/').size() > 2) {
			// com.jenkins.util.Utilities.printToConsoleOutput(this, [["RED", "FATAL: Improper branch name, more than one forward slash '/' not allowed"],
				// ["WHITE", env.BRANCH_NAME]], " : ")
			// return // exit pipeline prematurely
		// }

		// global flag
		this.env.BUILD_RUNNING = "false"

		String scmName = scmUrl.split("/")[scmUrl.split("/").size()-1].replace(".git", "")		
		echo "scmUrl: " + scmUrl
		echo "fullBranchName: " + fullBranchName
		echo "branchPrefix: " + branchPrefix
		echo "scmName: " + scmName

		// Reading pipeline configuration. This step will be performed by a default agent or master
		node {	
  		  String globalConfigString = this.libraryResource("util/default_settings.yaml")
    		String projectConfigString = this.libraryResource("$scmName/jenkinsConfig.yaml")
    		this.writeFile file: "global_config", text: globalConfigString
			this.writeFile file: "project_config", text: projectConfigString
    		Map globalConfig = this.readYaml file: "global_config"
    		Map projectConfig = this.readYaml file: "project_config"
    		config = com.jenkins.util.Utilities.reconcileConfig(projectConfig, globalConfig)
    		config.scm_repository = scmName
    		config.scm_url = scmUrl
    		config.scm_branch = fullBranchName
		}
		com.jenkins.util.Utilities.printToConsoleOutput(this, ["BLUE", new JsonBuilder(config).toPrettyString()])

		// Validating pipeline steps for the current branch
    	currentBuild.result = "SUCCESS"
	    for (wf in config.workflow) {
	    	if (wf.branch == branchPrefix) {
	     		workflow = wf
	     		com.jenkins.util.Utilities.printToConsoleOutput(this, [
	     			["GREEN", "Successfully detected configuration for current branch. Executing pipeline on"],
	     			["WHITE", branchPrefix]], " : ")
	     	}
	     	else if (wf.branch == config.default_branch) {
				// capture the "branch: 'default'" workflow, may need it later if branch is not explicitly matched
	     		defaultWorkflow = wf
	     	}
	    }
		if (workflow == null) {
			if (defaultWorkflow == null) {
				com.jenkins.util.Utilities.printToConsoleOutput(this, [
					["RED", "FATAL: No workflows match the current branch"],
					["WHITE", branchPrefix]], " : ")
		 		currentBuild.result = "ABORTED"
		 		return // exit pipeline prematurely
		 	}
		 	else {
		 		com.jenkins.util.Utilities.printToConsoleOutput(this, [
		 			["GREEN", "Successfully detected configuration for default branch. Executing pipeline on"],
		 			["WHITE", config.default_branch]], " : ")
		 		workflow = defaultWorkflow
		 	}
		}

		// Reading Jenkins master on which pipeline is triggered
    	String internalHostname = "docker"; //InetAddress.localHost.canonicalHostName
		if(true) {
			properties([buildDiscarder(logRotator(numToKeepStr: '5'))])
			com.jenkins.util.Utilities.printToConsoleOutput(this, [["BLUE", internalHostname],
				["CYAN", "using pipeline v1"]], " : ")
			config.pipeline_version = "v1"
			com.jenkins.util.Utilities.startTimer()
			node(config.agent) {
				for (step in workflow.steps) {
					step.each { stepName, stepConfig ->
						runStep(config, stepName, stepConfig)
					}
				}	
			}
		}
		else {
			com.jenkins.util.Utilities.printToConsoleOutput(this, ["RED", "Cannot recongzie this Jenkins master"])
			currentBuild.result = "ABORTED"
			return // exit pipeline prematurely
		}
	}
}

def runStep(config, stepName, stepConfig) {
	if (currentBuild.result == "SUCCESS" || currentBuild.result == "UNSTABLE") {
		def instance = this.class.classLoader.loadClass(stepName, true, false)?.newInstance()
		stepConfig = com.jenkins.util.Utilities.reconcileConfig(stepConfig, config)
		com.jenkins.util.Utilities.printToConsoleOutput(this, [["BLUE", "STAGE"], ["YELLOW", stepConfig.stage_name]], " : ")
		instance(stepConfig)
	}
	else if (currentBuild.result == "FAILURE") {
		if (stepName == "notification") {
			def instance = this.class.classLoader.loadClass(stepName, true, false)?.newInstance()
			stepConfig = com.jenkins.util.Utilities.reconcileConfig(stepConfig, config)
			com.jenkins.util.Utilities.printToConsoleOutput(this, [["BLUE", "STAGE"], ["YELLOW", stepConfig.stage_name]], " : ")
			instance(stepConfig)
		}
		else {
			com.jenkins.util.Utilities.printToConsoleOutput(this, [["BLUE", "Skipping"],
				["YELLOW", stepConfig.stage_name], ["BLUE", "due to previous step failing"]], " ")
		}
	}
	else if (currentBuild.result == "ABORTED") {
		com.jenkins.util.Utilities.printToConsoleOutput(this, [["BLUE", "Skipping"],
			["YELLOW", stepConfig.stage_name], ["BLUE", "due to previous step aborting"]], " ")
	}
	else {
		com.jenkins.util.Utilities.printToConsoleOutput(this, ["RED", "FATAL: cannot recognize " + currentBuild.result])
	}
}