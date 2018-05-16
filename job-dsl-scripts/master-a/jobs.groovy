//println System.getProperty("PWD")
println "${new File(__FILE__).parent}"

evaluate(new File("${new File(__FILE__).parent}/../tools.groovy"))

def jobs = [
	["java-hello-world-with-maven", "https://github.com/jabedhasan21/java-hello-world-with-maven"]
	]

createPipelineJobs(jobs)

def createPipelineJobs(jobs) { 
	for(int i=0; i<jobs.size(); i++)
	{
		println jobs[i][0]
		pipelineJob(jobs[i][0]) {
			definition {
				cps {
					sandbox()
					script("""
mavenPipeline {
	branch = "master"
	scmUrl = jobs[i][1]
}""".stripIndent())
				}
			}
			publishers {
				logRotator {
					numToKeep(5)
				}
			}
		}
	}
}