import com.JobUtils


def maven_deploy_jobs = [
	["devops/devops-war/DEV/devops-war-deploy", "https://github.com/dwillington/hello-world-war.git"],
	]

JobUtils.createMavenDeployPipelineJobs(this, maven_deploy_jobs)