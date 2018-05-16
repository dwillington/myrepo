//println System.getProperty("PWD")
println "${new File(__FILE__).parent}"

evaluate(new File("${new File(__FILE__).parent}/../../tools.groovy"))

def jobs = [
	["java-hello-world-with-maven", "https://github.com/jabedhasan21/java-hello-world-with-maven"]
	]

createPipelineJobs(jobs)