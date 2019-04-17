import com.JobUtils

folder("hello-world")
folder("hello-world/DEV")
folder("hello-world/SIT")

folder("hello-world-war")
folder("hello-world-war/DEV")

def jobs = [
	["hello-world/DEV/java-hello-world-with-maven", "https://github.com/LableOrg/java-maven-junit-helloworld"],
	["hello-world-war/DEV/hello-world-war", "https://github.com/dwillington/hello-world-war.git"],
	]


JobUtils.createPipelineJobs(this, jobs)