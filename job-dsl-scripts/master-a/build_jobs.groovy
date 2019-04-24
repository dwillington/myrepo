import com.JobUtils

folder("devops")
folder("devops/devops-war")
folder("devops/devops-war/DEV")

folder("devops/devops-jar")
folder("devops/devops-jar/DEV")
folder("devops/devops-jar/SIT")


def maven_jobs = [
	["devops/devops-jar/DEV/devops-jar-build", "https://github.com/LableOrg/java-maven-junit-helloworld"],
	["devops/devops-war/DEV/devops-war-build", "https://github.com/dwillington/hello-world-war.git"],
	]

JobUtils.createMavenPipelineJobs(this, maven_jobs)