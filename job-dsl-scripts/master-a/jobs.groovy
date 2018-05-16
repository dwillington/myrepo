evaluate(new File("../tools.groovy"))

def jobs = [
	["java-hello-world-with-maven", "https://github.com/jabedhasan21/java-hello-world-with-maven"]
	]

createPipelineJobs(jobs)