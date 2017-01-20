
// the following check is to allow this to skip when master seed job is run
if(binding.variables.containsKey("epic_name")) {

    job("poll-and-deploy-${epic_name}-solr") {
        scm {
            git {
                remote {
                    url("http://stash.homedepot.ca/scm/hdca/solr.git")
                    credentials('axa8962-credentials')
                    branch("$epic_name")
                }
            }
        }
        // triggers {
            // scm('H/10 * * * *')
        // }
        steps {
            maven {
                rootPOM('pom.xml')
                goals("clean assembly:assembly -Pbuild-solr-config,dev -Dsolr-type=master")
                mavenInstallation('apache-maven-3.3.9')
            }
            // shell("")
        }
        publishers {
            logRotator {
                numToKeep(10)
            }
        }
    }

}


