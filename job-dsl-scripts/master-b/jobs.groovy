pipelineJob("spring3-mvc-maven-xml-hello-world") {
    definition {
        cps {
			sandbox()
			script("""
mavenPipeline {
	branch = "master"
	scmUrl = "https://github.com/mkyong/spring3-mvc-maven-xml-hello-world"
}""".stripIndent())
		}
	}
    publishers {
        logRotator {
            numToKeep(5)
        }
    }
}