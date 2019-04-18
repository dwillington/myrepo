import com.JobUtils


def jobs = [
	]

pipelineJob('hello-world-war/DEV/hello-world-war-deploy') {
    definition {
        cps {
			sandbox()
            script("""mavenDeployPipeline {
	branch = "master"
	scmUrl = "$jobScmUrl"
}""".stripIndent()
        }
    }
	logRotator {
		numToKeep(5)
	}
}