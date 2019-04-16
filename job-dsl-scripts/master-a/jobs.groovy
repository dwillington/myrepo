import com.JobUtils

folder("hello-world")
folder("hello-world/DEV")
folder("hello-world/SIT")

def jobs = [
	["hello-world/DEV/java-hello-world-with-maven", "https://github.com/jabedhasan21/java-hello-world-with-maven"]
	]

JobUtils.createPipelineJobs(this, jobs)