def call(config) {
	def required = ["agent", "stage_name", "checkout_scm", "clean_up_workspace", "utility_type"]
	switch(config.utility_type) {
		case "RUN_COMMAND":
			required.add("command")
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