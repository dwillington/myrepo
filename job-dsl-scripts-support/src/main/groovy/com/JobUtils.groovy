package com;

class JobUtils {

    static void createPipelineJobs(context, pipelineType, java.util.ArrayList jobs) { 
        context.with {
            for(int i=0; i<jobs.size(); i++)
            {
                jobScmUrl = jobs[i][1];
                pipelineJob(jobs[i][0]) {
                    definition {
                        cps {
                            sandbox()
                            script("""$pipelineType {
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
    
    static void createMavenPipelineJobs(context, java.util.ArrayList jobs) { 
        createPipelineJobs(context, "mavenPipeline", jobs)
    }

    static void createMavenDeployPipelineJobs(context, java.util.ArrayList jobs) { 
        createPipelineJobs(context, "mavenDeployPipeline", jobs)
    }

}