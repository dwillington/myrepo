import com.JobUtils

folder("hello-world")
folder("hello-world/DEV")
folder("hello-world/SIT")

def jobs = [
	["hello-world/DEV/java-hello-world-with-maven", "https://github.com/LableOrg/java-maven-junit-helloworld"]
	]


JobUtils.createPipelineJobs(this, jobs)