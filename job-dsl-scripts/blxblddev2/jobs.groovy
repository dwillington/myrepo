folder("CIAD-TEST")
folder("CIAD-TEST/hello-world-war")
folder("CIAD-TEST/hello-world-war/DEV")

pipelineJob('CIAD-TEST/hello-world-war/DEV/hello-world-war-pipeline') {
	parameters {
        stringParam('REPO_URL', 'https://github.com/dwillington/hello-world-war.git')
    }
    definition {
        cps {
            script("""@Library('my-repo') _"""
			)
            sandbox()
        }
    }
}
