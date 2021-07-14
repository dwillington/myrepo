def call(config) {
	def required = ["agent", "stage_name", "checkout_scm", "clean_up_workspace", "utility_type"]
	switch(config.utility_type) {
		case "RUN_COMMAND":
			required.add("command")
			break
		case "RUN_JENKINS_JOB":
			required.add("job_path")
			required.add("job_parameter_map")
			required.add("job_wait_for_completion")
			required.add("job_ignore_exceptions")
			break
		case "GENERATE_SALESFORCE_CREDENTIAL":
			required.add("credential_file_path")
			required.add("salesforce_url")
			required.add("credential_id")
			required.add("token_id")
			break
		case "CREATE_BRANCH":
			required.add("branch")
			break
		case "CREATE_PULL_REQUEST":
			required.add("from")
			required.add("to")
			required.add("repository")
			required.add("project_key")
			break
		case "MERGE_PULL_REQUEST":
			required.add("repository")
			required.add("project_key")
			break
		case "CHECK_JENKINS_JOB_STATUS":
			required.add("job_link")
			required.add("target_branch")
			break
		case "CHECK_RUNNING_BUILD":
			required.add("query_endpoint")
			break
		case "CONFLUENCE":
			required.add("confluence_url")
			required.add("confluence_space")
			required.add("confluence_parent_id")
			break
		default:
			break
	}

	if (com.jenkins.util.Utilities.hasRequiredConfig(this, config, required)) {
		switch(config.utility_type) {
			case "RUN_COMMAND":
				def runCommand = new com.jenkins.util.RunCommand(this, config.stage_name)
				currentBuild.result = runCommand.run(config)
				break
			default:
				com.jenkins.util.Utilities.printToConsoleOutput(this, ["RED", "utility_type cannot be found"])
				currentBuild.result = "FAILURE"
				break
		}
	}
	else {
		currentBuild.result = "FAILURE"
	}
}