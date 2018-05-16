import com.JobUtils

def jobs = [
	["java-hello-world-with-maven", "https://github.com/jabedhasan21/java-hello-world-with-maven"]
	]

JobUtils.createPipelineJobs(this, jobs)