pipelineJob("java-hello-world-maven") {
    definition {
        cps {
			sandbox()
			script("""
mavenPipeline {
	branch = "master"
	url = "https://github.com/dantheman213/java-hello-world-maven.git"
}""".stripIndent())
		}
	}
}

