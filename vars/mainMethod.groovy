import groovy.json.*

def call(body) {
		// evaluate the body block, and collect configuration into the object
		def pipelineParams= [:]
		body.resolveStrategy = Closure.DELEGATE_FIRST
		body.delegate = pipelineParams
		body()

		Map config = null // stores entire job configuration

		// String scmUrl = params.REPO_URL
		// String scmName = scmUrl.split("/")[scmUrl.split("/").size()-1].replace(".git", "")		
		// echo scmName

		String scmUrl = pipelineParams.scmUrl
		String currentBranch = pipelineParams.branch
		echo pipelineParams.scmUrl
		echo pipelineParams.branch

		String scmName = scmUrl.split("/")[scmUrl.split("/").size()-1].replace(".git", "")		
		echo scmName

		// Reading pipeline configuration. This step will be performed by a default agent or master
		node {	
			String globalConfigString = this.libraryResource("com/td/jenkins/util/default_settings.yaml")
    		String projectConfigString = this.libraryResource("com/td/jenkins/$scmName/jenkinsConfig.yaml")
    		this.writeFile file: "global_config", text: globalConfigString
			this.writeFile file: "project_config", text: projectConfigString
    		Map globalConfig = this.readYaml file: "global_config"
    		Map projectConfig = this.readYaml file: "project_config"
    		config = com.td.jenkins.util.Utilities.reconcileConfig(projectConfig, globalConfig)
    		config.git_repository = scmName
		}
		com.td.jenkins.util.Utilities.printToConsoleOutput(this, ["BLUE", new JsonBuilder(config).toPrettyString()])

		// Validating pipeline steps for the current branch
    	currentBuild.result = "SUCCESS"
	    for (wf in config.workflow) {
	    	if (wf.branch == currentBranch) {
	     		workflow = wf
	     		com.td.jenkins.util.Utilities.printToConsoleOutput(this, [
	     			["GREEN", "Successfully detected configuration for current branch. Executing pipeline on"],
	     			["WHITE", currentBranch]], " : ")
	     	}
	     	else if (wf.branch == config.default_branch) {
	     		defaultWorkflow = wf
	     	}
	    }
		if (workflow == null) {
			if (defaultWorkflow == null) {
				com.td.jenkins.util.Utilities.printToConsoleOutput(this, [
					["RED", "FATAL: No workflows match the current branch"],
					["WHITE", currentBranch]], " : ")
		 		currentBuild.result = "ABORTED"
		 		return // exit pipeline prematurely
		 	}
		 	else {
		 		com.td.jenkins.util.Utilities.printToConsoleOutput(this, [
		 			["GREEN", "Successfully detected configuration for default branch. Executing pipeline on"],
		 			["WHITE", config.default_branch]], " : ")
		 		workflow = defaultWorkflow
		 	}
		}



}