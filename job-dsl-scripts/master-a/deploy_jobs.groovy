import com.JobUtils


def jobs = [
	]

pipelineJob('hello-world-war/DEV/hello-world-war-deploy') {
    definition {
        cps {
            script("""mavenDeployPipeline {
	branch = "master"
	scmUrl = "$jobScmUrl"
}""".stripIndent()
        }
    }
}