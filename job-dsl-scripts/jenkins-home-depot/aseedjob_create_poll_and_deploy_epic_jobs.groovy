
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
                localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
                jdk('JDK 8')
            }
            shell(
                "export HTTP_PROXY=http://str-www-proxy2-qa.homedepot.com:8080\n" + 
                "export HTTPS_PROXY=http://str-www-proxy2-qa.homedepot.com:8080\n" + 
                "/root/google-cloud-sdk/bin/gsutil cp target/homedepot-solr-0.0.1-SNAPSHOT.tar.gz gs://np-cadotcom.appspot.com/ci-builds/epic-builds/${epic_name}/solr/homedepot-solr-0.0.1-SNAPSHOT.tar.gz\n" + 
                "/root/myrepo/deploy-scripts/jenkins/trigger-jenkins-deploy.sh ${epic_name} solr\n"
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
                localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
                jdk('JDK 8')
            }
            shell(
                "export HTTP_PROXY=http://str-www-proxy2-qa.homedepot.com:8080\n" + 
                "export HTTPS_PROXY=http://str-www-proxy2-qa.homedepot.com:8080\n" + 
                "/root/google-cloud-sdk/bin/gsutil cp target/homedepot-httpd-0.0.1-SNAPSHOT.tar.gz gs://np-cadotcom.appspot.com/ci-builds/epic-builds/${epic_name}/apache/homedepot-httpd-0.0.1-SNAPSHOT.tar.gz\n" + 
                "/root/myrepo/deploy-scripts/jenkins/trigger-jenkins-deploy.sh ${epic_name} apache\n"
                 )
        }
        publishers {
            logRotator {
                numToKeep(10)
            }
        }
    }

    job("poll-and-deploy-${epic_name}-aem") {
        scm {
            git {
                remote {
                    url("http://stash.homedepot.ca/scm/hdca/aem.git")
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
                // goals("clean install -Pqp -Dcrx.url=http://ln0bd7.homedepot.com:4502 -DproxySet=true -DproxyHost=str-www-proxy2-qa -DproxyPort=8080")
                // TODO use an environment variable for the ip address
                goals("install -Pqp -Dcrx.failOnError=false -Dcrx.url=http://104.199.123.125: -DproxySet=true -DproxyHost=str-www-proxy2-qa -DproxyPort=8080 -Dcrx.password='admin' -pl '!homedepot-integration-tests'")
                // -pl '!test-content,!integration-tests,!integration-test-runners'
                mavenInstallation('apache-maven-3.3.9')
                localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
                jdk('JDK 8')
            }
            shell(
                "export HTTP_PROXY=http://str-www-proxy2-qa.homedepot.com:8080\n" + 
                "export HTTPS_PROXY=http://str-www-proxy2-qa.homedepot.com:8080\n" + 
                "/root/google-cloud-sdk/bin/gsutil cp homedepot-apps/target/homedepot.ca.homedepot-apps.zip gs://np-cadotcom.appspot.com/ci-builds/epic-builds/${epic_name}/aem/homedepot.ca.homedepot-apps.zip\n" + 
                "/root/myrepo/deploy-scripts/jenkins/trigger-jenkins-deploy.sh ${epic_name} aem\n" +
                ""
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
                    "cp -v hybris/config/dev/local.properties hybris/config/localDEV.properties\n" +
                    "rm hybris/bin/custom/homedepotca/homedepotcainitialdata/resources/homedepotcainitialdata/import/coredata/stores/homedepotca/solr.impex\n" +
                    "cp -v hybris/config/dev/solr.impex hybris/bin/custom/homedepotca/homedepotcainitialdata/resources/homedepotcainitialdata/import/coredata/stores/homedepotca/solr.impex\n" +
                    "cd hybris/bin/platform\n" +
                    ". ./setantenv.sh\n" +
                    "ant -Duseconfig=DEV clean all\n" +
                    "ant production\n"
                 )
            shell(
                "export HTTP_PROXY=http://str-www-proxy2-qa.homedepot.com:8080\n" + 
                "export HTTPS_PROXY=http://str-www-proxy2-qa.homedepot.com:8080\n" + 
                // commented out since the build creates prod based deployment which is not compatible with local.properties
                // "/root/google-cloud-sdk/bin/gsutil cp hybris/temp/hybris/hybrisServer/*.zip gs://np-cadotcom.appspot.com/ci-builds/epic-builds/${epic_name}/hybris/\n" + 
                // "/root/google-cloud-sdk/bin/gsutil cp hybris/xxx gs://np-cadotcom.appspot.com/ci-builds/epic-builds/${epic_name}/hybris/xxx\n" + 
                // "/root/google-cloud-sdk/bin/gsutil cp hybris/xxx gs://np-cadotcom.appspot.com/ci-builds/epic-builds/${epic_name}/hybris/xxx\n" + 
                "/root/myrepo/deploy-scripts/jenkins/trigger-jenkins-deploy.sh ${epic_name} hybris\n"
                 )
        }
        publishers {
            logRotator {
                numToKeep(10)
            }
        }
    }

}


