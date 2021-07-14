def call(config) {
	def required = ["agent", "stage_name", "checkout_scm", "clean_up_workspace", "maven_operation_type", "maven_command"]
	switch(config.maven_operation_type) {
		case "BUILD":
			break
		case "PUBLISH":
			required.add("version_strategy")
			break
		case "RELEASE":
			required.add("version_strategy")
			break
		default:
			break
	}

	if (com.jenkins.util.Utilities.hasRequiredConfig(this, config, required)) {
		switch(config.maven_operation_type) {
			case "BUILD":
				def mavenBuild = new com.jenkins.build.java.MavenBuild(this, config.stage_name)
				currentBuild.result = mavenBuild.run(config)
				break
			// case "PUBLISH":
				// def mavenPublish = new com.jenkins.publish.java.MavenPublish(this, config.stage_name)
				// currentBuild.result = mavenPublish.run(config)
				// break
			// case "RELEASE":
				// def mavenRelease = new com.jenkins.publish.java.MavenRelease(this, config.stage_name)
				// currentBuild.result = mavenRelease.run(config)
				// break
			default:
				com.jenkins.util.Utilities.printToConsoleOutput(this, ["RED", "maven_operation_type cannot be found"])
				currentBuild.result = "FAILURE"
		}
	}
	else {
		com.jenkins.util.Utilities.printToConsoleOutput(this, ["RED", "com.jenkins.util.Utilities.hasRequiredConfig(this, config, required) = false"])
		currentBuild.result = "FAILURE"
	}
}