def call(config) {
	def required = ["agent", "stage_name", "checkout_scm", "clean_up_workspace", "notification_type"]
	switch(config.notification_type) {
		case "EMAIL":
			required.add("notification_email_sender")
			required.add("notification_email_recipients")
			// required.add("email_type")
			break
		case "SCM":
			required.add("notification_scm_account_id")
			required.add("in_progress")
			break
		case "PUBLISH_REPORT":
			break
		default:
			break
	}

	if (com.jenkins.util.Utilities.hasRequiredConfig(this, config, required)) {
		switch(config.notification_type) {
			case "EMAIL":
				def emailNotification = new com.jenkins.notify.email.EmailNotification(this, config.stage_name)
				currentBuild.result = emailNotification.run(config)
				break
			case "SCM":
				def scmNotification = new com.jenkins.notify.scm.SCMNotification(this, config.stage_name)
				currentBuild.result = scmNotification.run(config)
				break
			default:
				com.jenkins.util.Utilities.printToConsoleOutput(this, ["RED", "notification_type cannot be found"])
				currentBuild.result = "FAILURE"
				break
		}
	}
	else {
		currentBuild.result = "FAILURE"
	}
}