import com.JobUtils


def jobs = [
	]

pipelineJob('devops-war/DEV/devops-war-deploy') {
    definition {
        cps {
			sandbox()
            script("""mavenDeployPipeline {
	branch = "master"
	scmUrl = "https://github.com/dwillington/hello-world-war.git"
}""".stripIndent())
		}
	}
	logRotator {
		numToKeep(5)
	}
}