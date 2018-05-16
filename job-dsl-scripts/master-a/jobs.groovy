println new File(System.getProperty("user.dir")).name;
println ${WORKSPACE};

evaluate(new File("job-dsl-scripts/tools.groovy"))

def jobs = [
	["java-hello-world-with-maven", "https://github.com/jabedhasan21/java-hello-world-with-maven"]
	]

createPipelineJobs(jobs)