import com.JobUtils


def maven_deploy_jobs = [
	["devops-war/DEV/devops-war-build", "https://github.com/dwillington/hello-world-war.git"],
	]

JobUtils.createMavenPipelineJobs(this, maven_deploy_jobs)