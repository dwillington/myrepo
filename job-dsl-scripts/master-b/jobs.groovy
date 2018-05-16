def jobs = [
	["spring3-mvc-maven-xml-hello-world", "https://github.com/mkyong/spring3-mvc-maven-xml-hello-world"]
		   ]

for(int i=0; i<jobs.length; i++)
{
	pipelineJob($jobs[i][0]) {
		definition {
			cps {
				sandbox()
				script("""
mavenPipeline {
	branch = "master"
	scmUrl = $jobs[i][1]
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