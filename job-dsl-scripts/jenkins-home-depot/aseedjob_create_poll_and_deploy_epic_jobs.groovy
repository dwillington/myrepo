
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
                jdk('JDK 8')
            }
            shell(
                "export HTTP_PROXY=http://str-www-proxy2-qa.homedepot.com:8080\n" + 
                "export HTTPS_PROXY=http://str-www-proxy2-qa.homedepot.com:8080\n" + 
                "/root/google-cloud-sdk/bin/gsutil cp target/homedepot-solr-0.0.1-SNAPSHOT.tar.gz gs://np-cadotcom.appspot.com/ci-builds/epic-builds/epic2/solr/homedepot-solr-0.0.1-SNAPSHOT.tar.gz\n" + 
                "/root/myrepo/deploy-scripts/jenkins/trigger-jenkins-deploy.sh epic2 solr\n"
                 )
        }
        publishers {
            logRotator {
                numToKeep(10)
            }
        }
    }

    job("poll-and-deploy-${epic_name}-apache") {
        scm {
            git {
                remote {
                    url("http://stash.homedepot.ca/scm/hdca/apache.git")
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
                goals("clean assembly:assembly -Pbuild-httpd-config,dev -DproxySet=true -DproxyHost=str-www-proxy2-qa -DproxyPort=8080")
                mavenInstallation('apache-maven-3.3.9')
                jdk('JDK 8')
            }
            shell(
                "export HTTP_PROXY=http://str-www-proxy2-qa.homedepot.com:8080\n" + 
                "export HTTPS_PROXY=http://str-www-proxy2-qa.homedepot.com:8080\n" + 
                "/root/google-cloud-sdk/bin/gsutil cp target/homedepot-httpd-0.0.1-SNAPSHOT.tar.gz gs://np-cadotcom.appspot.com/ci-builds/epic-builds/epic2/apache/homedepot-httpd-0.0.1-SNAPSHOT.tar.gz\n" + 
                "/root/myrepo/deploy-scripts/jenkins/trigger-jenkins-deploy.sh epic2 apache\n"
                 )
        }
        publishers {
            logRotator {
                numToKeep(10)
            }
        }
    }

    job("poll-and-deploy-${epic_name}-hybris") {
        scm {
            git {
                remote {
                    url("http://stash.homedepot.ca/scm/hdca/hybris-suite.git")
                    credentials('axa8962-credentials')
                    branch("$epic_name")
                }
                extensions {
                    relativeTargetDirectory('repo')
                }
            }
        }
        // triggers {
            // scm('H/10 * * * *')
        // }
        steps {
            shell(
                    "rm -rf hybris\n" +
                    "mkdir hybris\n" +
                    "cp -R /bamboo/data/hybris_platform/hybris_5_4_0_0/* hybris\n" +
                    "cp -R repo/hybris/* hybris/.\n" +
                    "cp -p /bamboo/data/hybris_platform/hybrislicence.jar hybris/config/licence/hybrislicence.jar\n" +
                    "cd hybris/bin/platform\n" +
                    ". ./setantenv.sh\n" +
                    "cp -v hybris/config/dev/local.properties hybris/config/localDEV.properties\n" +
                    "rm hybris/bin/custom/homedepotca/homedepotcainitialdata/resources/homedepotcainitialdata/import/coredata/stores/homedepotca/solr.impex\n" +
                    "cp -v hybris/config/dev/solr.impex hybris/bin/custom/homedepotca/homedepotcainitialdata/resources/homedepotcainitialdata/import/coredata/stores/homedepotca/solr.impex\n" +
                    "ant -Duseconfig=DEV clean all\n" +
                    "ant production\n"
                 )
            // shell(
                // "export HTTP_PROXY=http://str-www-proxy2-qa.homedepot.com:8080\n" + 
                // "export HTTPS_PROXY=http://str-www-proxy2-qa.homedepot.com:8080\n" + 
                // "/root/google-cloud-sdk/bin/gsutil cp hybris/homedepot-httpd-0.0.1-SNAPSHOT.tar.gz gs://np-cadotcom.appspot.com/ci-builds/epic-builds/epic2/hybris/homedepot-httpd-0.0.1-SNAPSHOT.tar.gz\n" + 
                // "/root/myrepo/deploy-scripts/jenkins/trigger-jenkins-deploy.sh epic2 hybris\n"
                 // )
        }
        publishers {
            logRotator {
                numToKeep(10)
            }
        }
    }

}


