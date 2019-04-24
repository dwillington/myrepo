import com.JobUtils

folder("hello-world")
folder("hello-world/DEV")
folder("hello-world/SIT")

folder("devops")
folder("devops/devops-war")
folder("devops/devops-war/DEV")

def maven_jobs = [
	["hello-world/DEV/java-hello-world-with-maven", "https://github.com/LableOrg/java-maven-junit-helloworld"],
	["devops/devops-war/DEV/devops-war-build", "https://github.com/dwillington/hello-world-war.git"],
	]

JobUtils.createMavenPipelineJobs(this, maven_jobs)