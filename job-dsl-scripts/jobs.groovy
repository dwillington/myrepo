pipelineJob("java-hello-world-maven") {
    definition {
        cps {
			sandbox()
			script("""
mavenPipeline {
	branch = "master"
	url = "https://github.com/jabedhasan21/java-hello-world-with-maven"
}""".stripIndent())
		}
	}
    publishers {
        logRotator {
            numToKeep(5)
        }
    }
}