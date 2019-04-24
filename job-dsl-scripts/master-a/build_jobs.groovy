import com.JobUtils

folder("devops")
folder("devops/devops-war")
folder("devops/devops-war/DEV")
folder("devops/devops-war/RELEASE")

folder("devops/devops-common-jar")
folder("devops/devops-common-jar/DEV")

def maven_jobs = [
	["devops/devops-common-jar/DEV/devops-common-jar-build", "https://github.com/LableOrg/java-maven-junit-helloworld"],
	["devops/devops-war/DEV/devops-war-build", "https://github.com/dwillington/hello-world-war.git"],
	["devops/devops-war/RELEASE/devops-war-build", "https://github.com/dwillington/hello-world-war.git"],
	]

JobUtils.createMavenPipelineJobs(this, maven_jobs)