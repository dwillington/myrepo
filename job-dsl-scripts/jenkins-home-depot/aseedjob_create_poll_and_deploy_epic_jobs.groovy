
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
                    url("http://stash.homedepot.ca/scm/hdca/hybris.git")
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
                goals("clean assembly:assembly -Pbuild-httpd-config,qp -DproxySet=true -DproxyHost=str-www-proxy2-qa -DproxyPort=8080")
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

}


