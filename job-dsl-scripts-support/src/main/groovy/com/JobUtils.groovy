package com;

class JobUtils {

	static void createPipelineJobs(jobs[][]) { 
		for(int i=0; i<jobs.size(); i++)
		{
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
}