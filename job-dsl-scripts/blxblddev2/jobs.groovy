folder("CIAD-TEST")
folder("CIAD-TEST/hello-world-war")
folder("CIAD-TEST/hello-world-war/DEV")

pipelineJob('CIAD-TEST/hello-world-war/DEV/hello-world-war-pipeline') {
	// parameters {
        // stringParam('REPO_URL', 'https://github.com/dwillington/hello-world-war.git')
        // stringParam('BRANCH', 'master')
    // }
    definition {
        cps {
            script("""@Library('myrepo') _
mainMethod{
    // scmUrl = "https://github.com/dwillington/hello-world-war.git"
	scmUrl = "http://blxblddev3.transunion.ca:10002/apps/hello-world-war"
    branch = "default"
}"""
			)
            sandbox()
        }
    }
}

// multibranchPipelineJob('CIAD-TEST/hello-world-war/DEV/hello-world-war-multibranch-pipeline-job-dsl') {
    // branchSources {
        // hg ('http://blxblddev3.transunion.ca:10002/apps/hello-world-war') {
            // installation('builtin')
            // credentialsId('jenkins-rhodecode')
        // }
    // }
    // orphanedItemStrategy {
        // discardOldItems {
            // numToKeep(1)
        // }
    // }
// }