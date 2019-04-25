import com.JobUtils

folder("devops-project")
folder("devops-project/devops-war-repo")
folder("devops-project/devops-war-repo/DEV")
folder("devops-project/devops-war-repo/RELEASE")

folder("devops-project/devops-common-jar-repo")
folder("devops-project/devops-common-jar-repo/DEV")

def maven_jobs = [
 	["devops-project/devops-common-jar-repo/DEV/devops-common-jar-build", "https://github.com/LableOrg/java-maven-junit-helloworld"],
	["devops-project/devops-war-repo/DEV/devops-war-build", "https://github.com/dwillington/hello-world-war.git"],
	["devops-project/devops-war-repo/RELEASE/devops-war-build", "https://github.com/dwillington/hello-world-war.git"],
	]

JobUtils.createMavenPipelineJobs(this, maven_jobs)