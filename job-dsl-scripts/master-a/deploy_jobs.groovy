import com.JobUtils


def maven_deploy_jobs = [
    ["devops-project/devops-war-repo/DEV/devops-war-deploy", "https://github.com/dwillington/hello-world-war.git"],
    ["devops-project/devops-war-repo/RELEASE/devops-war-deploy", "https://github.com/dwillington/hello-world-war.git"],
    ]

JobUtils.createMavenDeployPipelineJobs(this, maven_deploy_jobs)