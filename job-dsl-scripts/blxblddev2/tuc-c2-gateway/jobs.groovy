folder("CIAD-TEST")
folder("CIAD-TEST/apps")
folder("CIAD-TEST/apps/services")
folder("CIAD-TEST/apps/services/tuc-c2-gateway")
folder("CIAD-TEST/apps/services/tuc-c2-gateway/DEV")

// pipelineJob('CIAD-TEST/apps/services/tuc-c2-gateway/DEV/tuc-c2-gateway-pipeline') {
    // definition {
        // cps {
            // script("""@Library('myrepo') _
// mainMethod{
	// scmUrl = "http://blxblddev3.transunion.ca:10002/apps/services/tuc-c2-gateway"
    // branch = "default"
// }"""
			// )
            // sandbox()
        // }
    // }
// }

multibranchPipelineJob('CIAD-TEST/apps/services/tuc-c2-gateway/DEV/tuc-c2-gateway-multibranch-pipeline-dsl') {
    branchSources {
        hg ('http://blxblddev3.transunion.ca:10002/apps/services/tuc-c2-gateway') {
            installation('builtin')
            credentialsId('jenkins-rhodecode')
        }
    }
    orphanedItemStrategy {
        discardOldItems {
            numToKeep(1)
        }
    }
}