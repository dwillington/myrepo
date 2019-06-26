import groovy.json.*

def call(body) {
	ansiColor('xterm') {
		// evaluate the body block, and collect configuration into the object
		def pipelineParams= [:]
		body.resolveStrategy = Closure.DELEGATE_FIRST
		body.delegate = pipelineParams
		body()

		Map config = null // stores entire job configuration
	    Map workflow = null // stores steps for current branch 
		Map defaultWorkflow = null // stores steps for the default branch

		String scmUrl;
		String currentBranch;

		if(env.BRANCH_NAME) {
		    // scmUrl = this.scm.getUserRemoteConfigs()[0].getUrl()
			scmUrl = this.scm.getSource()
			currentBranch = env.BRANCH_NAME.split('/')[0]
		}
		else {
			scmUrl = pipelineParams.scmUrl
			currentBranch = pipelineParams.branch
		}

		// if (env.BRANCH_NAME.split('/').size() > 2) {
			// com.td.jenkins.util.Utilities.printToConsoleOutput(this, [["RED", "FATAL: Improper branch name, more than one forward slash '/' not allowed"],
				// ["WHITE", env.BRANCH_NAME]], " : ")
// `			return // exit pipeline prematurely
		// }    

		// global flag
		// this.env.BUILD_RUNNING = "false"

		echo "scmUrl: " + scmUrl
		echo "currentBranch: " + currentBranch

		String scmName = scmUrl.split("/")[scmUrl.split("/").size()-1].replace(".git", "")		
		echo scmName

		// Reading pipeline configuration. This step will be performed by a default agent or master
		node {	
			String globalConfigString = this.libraryResource("com/tuc/jenkins/util/default_settings.yaml")
    		String projectConfigString = this.libraryResource("com/tuc/jenkins/$scmName/jenkinsConfig.yaml")
    		this.writeFile file: "global_config", text: globalConfigString
			this.writeFile file: "project_config", text: projectConfigString
    		Map globalConfig = this.readYaml file: "global_config"
    		Map projectConfig = this.readYaml file: "project_config"
    		config = com.tuc.jenkins.util.Utilities.reconcileConfig(projectConfig, globalConfig)
    		config.scm_repository = scmName
    		config.scm_url = scmUrl
    		config.scm_branch = currentBranch
		}
		com.tuc.jenkins.util.Utilities.printToConsoleOutput(this, ["BLUE", new JsonBuilder(config).toPrettyString()])

		// Validating pipeline steps for the current branch
    	currentBuild.result = "SUCCESS"
	    for (wf in config.workflow) {
	    	if (wf.branch == currentBranch) {
	     		workflow = wf
	     		com.tuc.jenkins.util.Utilities.printToConsoleOutput(this, [
	     			["GREEN", "Successfully detected configuration for current branch. Executing pipeline on"],
	     			["WHITE", currentBranch]], " : ")
	     	}
	     	else if (wf.branch == config.default_branch) {
	     		defaultWorkflow = wf
	     	}
	    }
		if (workflow == null) {
			if (defaultWorkflow == null) {
				com.tuc.jenkins.util.Utilities.printToConsoleOutput(this, [
					["RED", "FATAL: No workflows match the current branch"],
					["WHITE", currentBranch]], " : ")
		 		currentBuild.result = "ABORTED"
		 		return // exit pipeline prematurely
		 	}
		 	else {
		 		com.tuc.jenkins.util.Utilities.printToConsoleOutput(this, [
		 			["GREEN", "Successfully detected configuration for default branch. Executing pipeline on"],
		 			["WHITE", config.default_branch]], " : ")
		 		workflow = defaultWorkflow
		 	}
		}

		// Reading Jenkins master on which pipeline is triggered
    	String internalHostname = InetAddress.localHost.canonicalHostName
		if(true) {
			properties([buildDiscarder(logRotator(numToKeepStr: '10'))])
			com.tuc.jenkins.util.Utilities.printToConsoleOutput(this, [["BLUE", internalHostname],
				["CYAN", "using pipeline v2"]], " : ")
			config.pipeline_version = "v2"
			com.tuc.jenkins.util.Utilities.startTimer()
			node(config.agent) {
				for (step in workflow.steps) {
					step.each { stepName, stepConfig ->
						runStep(config, stepName, stepConfig)
					}
				}	
			}
		}
		else {
			com.tuc.jenkins.util.Utilities.printToConsoleOutput(this, ["RED", "Cannot recongzie this Jenkins master"])
			currentBuild.result = "ABORTED"
			return // exit pipeline prematurely
		}
	}
}

def runStep(config, stepName, stepConfig) {
	if (currentBuild.result == "SUCCESS" || currentBuild.result == "UNSTABLE") {
		def instance = this.class.classLoader.loadClass(stepName, true, false)?.newInstance()
		stepConfig = com.tuc.jenkins.util.Utilities.reconcileConfig(stepConfig, config)
		com.tuc.jenkins.util.Utilities.printToConsoleOutput(this, [["BLUE", "STAGE"], ["YELLOW", stepConfig.stage_name]], " : ")
		instance(stepConfig)
	}
	else if (currentBuild.result == "FAILURE") {
		if (stepName == "notification") {
			def instance = this.class.classLoader.loadClass(stepName, true, false)?.newInstance()
			stepConfig = com.tuc.jenkins.util.Utilities.reconcileConfig(stepConfig, config)
			com.tuc.jenkins.util.Utilities.printToConsoleOutput(this, [["BLUE", "STAGE"], ["YELLOW", stepConfig.stage_name]], " : ")
			instance(stepConfig)
		}
		else {
			com.tuc.jenkins.util.Utilities.printToConsoleOutput(this, [["BLUE", "Skipping"],
				["YELLOW", stepConfig.stage_name], ["BLUE", "due to previous step failing"]], " ")
		}
	}
	else if (currentBuild.result == "ABORTED") {
		com.tuc.jenkins.util.Utilities.printToConsoleOutput(this, [["BLUE", "Skipping"],
			["YELLOW", stepConfig.stage_name], ["BLUE", "due to previous step aborting"]], " ")
	}
	else {
		com.tuc.jenkins.util.Utilities.printToConsoleOutput(this, ["RED", "FATAL: cannot recognize " + currentBuild.result])
	}
}