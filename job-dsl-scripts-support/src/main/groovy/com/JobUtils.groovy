package com;

class JobUtils {

	static void createPipelineJobs(context, java.util.ArrayList jobs) { 
		context.with {
			for(int i=0; i<jobs.size(); i++)
			{
				jobScmUrl = jobs[i][1];
				pipelineJob(jobs[i][0]) {
					definition {
						cps {
							sandbox()
							script("""mavenPipeline {
	branch = "master"
	scmUrl = "$jobScmUrl"
}""".stripIndent())
						}
					}
					logRotator {
						numToKeep(5)
					}
				}
			}
		}
	}
}