emailList = 'AMJAD_ASHRAF@homedepot.com' //SAIPRASADH_SEKAR@homedepot.com,
hd_aem_host = '172.24.103.213'
sonar_host = '104.198.108.236'
// property("sonar.host.url", "http://172.24.100.252")

// the following check is to allow this to skip when master seed job is run
if(binding.variables.containsKey("epic_name")) {

    pipelineJob("build-deploy-${epic_name}-solr-pipeline") {
        definition {
            cps {
                sandbox()
                script (
            "node() {" + "\n" +
            "  stage 'Build'" + "\n" + 
            "    try {" + "\n" +
            "      def build = build job: 'build-" + "${epic_name}" + "-solr', wait: true" + "\n" +
            "    } finally {}" + "\n" +
            "  stage 'Deploy'" + "\n" +
            "    try {" + "\n" +
            "      def build = build job: 'deploy-" + "${epic_name}" + "-solr', wait: true" + "\n" +
            "    } finally {}" + "\n" +
            "}" + "\n" +
            "" + "\n" +
            ""
                )
            }
        }
    }

    job("build-${epic_name}-solr") {
        scm {
            git {
                remote {
                    url("http://stash.homedepot.ca/scm/hdca/solr.git")
                    credentials('axa8962-credentials')
                    branch("master")
                }
            }
        }
        steps {
            shell(
                "sleep 30\n" + 
                "cd homedepot-solr/server/solr\n" + 
                "tar -zcvf solr-configsets.tar.gz configsets\n" + 
                "export HTTP_PROXY=http://str-www-proxy2-qa.homedepot.com:8080\n" + 
                "export HTTPS_PROXY=http://str-www-proxy2-qa.homedepot.com:8080\n" + 
                "/root/google-cloud-sdk/bin/gsutil cp solr-configsets.tar.gz gs://np-cadotcom.appspot.com/ci-builds/epic-builds/${epic_name}/solr/solr-configsets.tar.gz\n" + 
                "/root/google-cloud-sdk/bin/gsutil rm gs://np-cadotcom.appspot.com/ci-builds/epic-deploy-results/${epic_name}/solr/deploy.result\n" + 
                "/root/myrepo/deploy-scripts/jenkins/trigger-jenkins-deploy.sh ${epic_name} solr\n"
                 )
        }
        publishers {
            extendedEmail {
                recipientList(emailList)
                contentType('text/html')
            }
            logRotator {
                numToKeep(5)
            }
            // downstream("sonar-${epic_name}-solr", 'SUCCESS')
        }
    }

    job("deploy-${epic_name}-solr") {
        scm {
            git {
                remote {
                    url("https://github.com/dwillington/myrepo.git")
                    credentials('dwillington-credentials')
                    branch('gcloud')
                }
            }
        }
        steps {
            shell(
                    "deploy-scripts/jenkins/poll-deploy-results.sh ${epic_name} solr" +
                    ""
                 )
        }
        publishers {
            extendedEmail {
                recipientList(emailList)
                contentType('text/html')
            }
            logRotator {
                numToKeep(5)
            }
        }
    }

    job("poll-and-build-deploy-${epic_name}-solr") {
        scm {
            git {
                remote {
                    url("http://stash.homedepot.ca/scm/hdca/solr.git")
                    credentials('axa8962-credentials')
                    branch("master")
                }
            }
        }
        // triggers {
            // scm('H/10 * * * *')
        // }
        steps {
            shell(
                "cd homedepot-solr/server/solr\n" + 
                "tar -zcvf solr-configsets.tar.gz configsets\n" + 
                "export HTTP_PROXY=http://str-www-proxy2-qa.homedepot.com:8080\n" + 
                "export HTTPS_PROXY=http://str-www-proxy2-qa.homedepot.com:8080\n" + 
                "/root/google-cloud-sdk/bin/gsutil cp solr-configsets.tar.gz gs://np-cadotcom.appspot.com/ci-builds/epic-builds/${epic_name}/solr/solr-configsets.tar.gz\n" + 
                "/root/myrepo/deploy-scripts/jenkins/trigger-jenkins-deploy.sh ${epic_name} solr\n"
                 )
        }
        publishers {
            extendedEmail {
                recipientList(emailList)
                contentType('text/html')
            }
            logRotator {
                numToKeep(5)
            }
            downstream("sonar-${epic_name}-solr", 'SUCCESS')
        }
    }

    job("poll-and-build-deploy-${epic_name}-apache") {
        scm {
            git {
                remote {
                    url("http://stash.homedepot.ca/scm/hdca/apache.git")
                    credentials('axa8962-credentials')
                    branch("master")
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
                numToKeep(5)
           }
            downstream("sonar-${epic_name}-apache", 'SUCCESS')
        }
    }

    job("poll-and-build-deploy-${epic_name}-aem") {
        scm {
            git {
                remote {
                    url("http://stash.homedepot.ca/scm/hdca/aem.git")
                    credentials('axa8962-credentials')
                    branch("master")
                }
            }
        }
        // triggers {
            // scm('H/10 * * * *')
        // }
        steps {
            shell(
                "sed -i \"342i <failOnError>false</failOnError>\" homedepot-apps/pom.xml\n" + 
                "sed -i \"27i <failOnError>false</failOnError>\" homedepot-integration-tests/test-content/pom.xml\n" + 
                "sed -i \"27i <useProxy>false</useProxy>\" homedepot-integration-tests/test-content/pom.xml\n" + 
                // "sed -i -e \"s/<useProxy>false<\/useProxy>/<useProxy>true<\/useProxy>/g\" homedepot-apps/pom.xml\n" + 
                "grep -C 2 failOnError homedepot-apps/pom.xml\n" + 
                ""
            )
            maven {
                rootPOM('pom.xml')
                // goals("clean install -Pqp -Dcrx.url=http://ln0bd7.homedepot.com:4502 -DproxySet=true -DproxyHost=str-www-proxy2-qa -DproxyPort=8080")
                // TODO use an environment variable for the ip address
                goals("install -Pqp -Dcrx.url=http://${hd_aem_host}:4503 -DproxySet=true -DproxyHost=str-www-proxy2-qa -DproxyPort=8080 -Dcrx.password='admin' -pl '!homedepot-integration-tests/integration-test-runners,!homedepot-integration-tests/integration-tests,!homedepot-integration-tests/test-content'")
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
                numToKeep(5)
            }
            downstream("restart-aem-${hd_aem_host}", 'SUCCESS')
        }
    }

    job("poll-and-build-deploy-${epic_name}-hybris") {
        scm {
            git {
                remote {
                    url("http://stash.homedepot.ca/scm/hdca/hybris-suite.git")
                    credentials('axa8962-credentials')
                    // branch("$epic_name")
                    branch("develop")
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
                    "cp -R /bamboo/data/hybris_platform/hybris_6_2_0_0/hybris_6_2_0_0.zip hybris\n" +
                    "cd hybris\n" + 
                    "unzip -o hybris_6_2_0_0.zip\n" + 
                    "cd -\n" + 
                    "cp -R repo/hybris/* hybris/.\n" +
                    "cp -p /bamboo/data/hybris_platform/hybrislicence.jar hybris/config/licence/hybrislicence.jar\n" +
                    "cp -v hybris/config/dev/local.properties hybris/config/localDEV.properties\n" +
                    "rm hybris/bin/custom/homedepotca/homedepotcainitialdata/resources/homedepotcainitialdata/import/coredata/stores/homedepotca/solr.impex\n" +
                    "cp -v hybris/config/dev/solr.impex hybris/bin/custom/homedepotca/homedepotcainitialdata/resources/homedepotcainitialdata/import/coredata/stores/homedepotca/solr.impex\n" +
                    "cd hybris/bin/platform\n" +
                    ". ./setantenv.sh\n" +
                    "export ANT_OPTS=\"-Xmx512m -XX:MaxPermSize=128M -Dhttp.proxyHost=str-www-proxy2-qa -Dhttp.proxyPort=8080\"\n" + 
                    "ant -Duseconfig=DEV clean all\n" +
                    "ant production\n" + 
                    ""
                 )
            shell(
                "export HTTP_PROXY=http://str-www-proxy2-qa.homedepot.com:8080\n" + 
                "export HTTPS_PROXY=http://str-www-proxy2-qa.homedepot.com:8080\n" + 
                "/root/google-cloud-sdk/bin/gsutil cp hybris/temp/hybris/hybrisServer/*.zip gs://np-cadotcom.appspot.com/ci-builds/epic-builds/${epic_name}/hybris/\n" + 
                "/root/myrepo/deploy-scripts/jenkins/trigger-jenkins-deploy.sh ${epic_name} hybris\n" +
                ""
                 )
        }
        publishers {
            logRotator {
                numToKeep(5)
            }
        }
    }

    job("restart-aem-${hd_aem_host}") {
        steps {
            shell(
                "ssh root@${hd_aem_host} /opt/adobe/publish/crx-quickstart/bin/stop\n" + 
                "ssh root@${hd_aem_host} /root/aem_setup_publish.sh\n" + 
                ""
            )
        }
        publishers {
            logRotator {
                numToKeep(5)
            }
        }
    }

    // sonar job definitions
if(false)
{
    job("sonar-${epic_name}-solr") {
        scm {
            git {
                remote {
                    url("http://stash.homedepot.ca/scm/hdca/solr.git")
                    credentials('axa8962-credentials')
                    branch("master")
                }
            }
        }
        steps {
            maven {
                rootPOM('pom.xml')
                goals("assembly:assembly sonar:sonar -Pbuild-solr-config,dev -Dsolr-type=master -DproxySet=true -DproxyHost=str-www-proxy2-qa -DproxyPort=8080")
                property("sonar.host.url", "http://${sonar_host}")
                mavenInstallation('apache-maven-3.3.9')
                localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
                jdk('JDK 8')
            }
        }
        publishers {
            logRotator {
                numToKeep(5)
            }
        }
    }

    job("sonar-${epic_name}-apache") {
        scm {
            git {
                remote {
                    url("http://stash.homedepot.ca/scm/hdca/apache.git")
                    credentials('axa8962-credentials')
                    branch("master")
                }
            }
        }
        steps {
            maven {
                rootPOM('pom.xml')
                goals("assembly:assembly -Pbuild-httpd-config,dev sonar:sonar -DproxySet=true -DproxyHost=str-www-proxy2-qa -DproxyPort=8080")
                property("sonar.host.url", "http://${sonar_host}")
                mavenInstallation('apache-maven-3.3.9')
                localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
                jdk('JDK 8')
            }
        }
        publishers {
            logRotator {
                numToKeep(5)
            }
        }
    }
    
    job("sonar-${epic_name}-aem") {
        scm {
            git {
                remote {
                    url("http://stash.homedepot.ca/scm/hdca/aem.git")
                    credentials('axa8962-credentials')
                    branch("master")
                }
            }
        }
        steps {
            shell(
                "sed -i \"342i <failOnError>false</failOnError>\" homedepot-apps/pom.xml\n" + 
                "sed -i \"27i <failOnError>false</failOnError>\" homedepot-integration-tests/test-content/pom.xml\n" + 
                "sed -i \"27i <useProxy>false</useProxy>\" homedepot-integration-tests/test-content/pom.xml\n" + 
                "grep -C 2 failOnError homedepot-apps/pom.xml\n" + 
                ""
            )
            maven {
                rootPOM('pom.xml')
                goals("install -Pqp -Dcrx.url=http://${hd_aem_host}:4503 -DproxySet=true -DproxyHost=str-www-proxy2-qa -DproxyPort=8080 -Dcrx.password='admin' sonar:sonar -pl '!homedepot-integration-tests/integration-test-runners,!homedepot-integration-tests/integration-tests,!homedepot-integration-tests/test-content'")
                property("sonar.host.url", "http://${sonar_host}")
                mavenInstallation('apache-maven-3.3.9')
                localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
                jdk('JDK 8')
            }
        }
        publishers {
            logRotator {
                numToKeep(5)
            }
            downstream("restart-aem-${hd_aem_host}", 'SUCCESS')
        }
    }

}   

    job("sonar-${epic_name}-hybris") {
        scm {
            git {
                remote {
                    url("http://stash.homedepot.ca/scm/hdca/hybris-suite.git")
                    credentials('axa8962-credentials')
                    branch("master")
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
                    "cp -R /bamboo/data/hybris_platform/hybris_6_2_0_0/hybris_6_2_0_0.zip hybris\n" +
                    "cd hybris\n" + 
                    "unzip -o hybris_6_2_0_0.zip\n" + 
                    "cd -\n" + 
                    "cp -R repo/hybris/* hybris/.\n" +
                    "cp -p /bamboo/data/hybris_platform/hybrislicence.jar hybris/config/licence/hybrislicence.jar\n" +
                    "cd hybris/bin/platform\n" +
                    ". ./setantenv.sh\n" +
                    "cp -v hybris/config/dev/local.properties hybris/config/localDEV.properties\n" +
                    "rm hybris/bin/custom/homedepotca/homedepotcainitialdata/resources/homedepotcainitialdata/import/coredata/stores/homedepotca/solr.impex\n" +
                    "cp -v hybris/config/dev/solr.impex hybris/bin/custom/homedepotca/homedepotcainitialdata/resources/homedepotcainitialdata/import/coredata/stores/homedepotca/solr.impex\n" +
                    "export ANT_OPTS=\"-Xmx512m -XX:MaxPermSize=128M -Dhttp.proxyHost=str-www-proxy2-qa -Dhttp.proxyPort=8080\"\n" + 
                    "ant -Duseconfig=DEV clean all\n" +
                    "ant production\n" + 
                    "ant sonar -Dsonar.host.url=http://${sonar_host} -Dsonar.login=admin -Dsonar.password=admin\n" + 
                    ""
                 )
        }
        publishers {
            logRotator {
                numToKeep(5)
            }
        }
    }

}


