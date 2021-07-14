package com.jenkins.notify

public abstract class Notifier extends com.jenkins.Step {
	
	protected abstract void notify(Map config)

	protected void execute(Map config) throws Exception {
		notify(config)
	}

	protected Boolean sendNotification(Map config) throws Exception {
		config.notification_cases.contains(scope.currentBuild.result) ? true : false
	}
}