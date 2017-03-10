// credentials
// dwillington-credentials
// axa8962-credentials / qa02test!

// a-seed-job
// https://github.com/dwillington/myrepo.git
// job-dsl-scripts/jenkins-home-depot/**/*.groovy

// JENKINS_JAVA_OPTIONS="-Djava.awt.headless=true -Dhttp.proxyHost=str-www-proxy2-qa.homedepot.com -Dhttp.proxyPort=8080"
// JENKINS_JAVA_OPTIONS="-Dhttp.proxyHost=str-www-proxy2-qa.homedepot.com -Dhttp.proxyPort=8080"

// icp global maven settins.xml (everytime I guess)
// wow major hard coding below!
//println "/bin/cp -rf /var/lib/jenkins/workspace/a-seed-job/job-dsl-scripts/sync-settings/settings.xml /var/lib/jenkins/settings.xml".execute().text

def sout = new StringBuilder(), serr = new StringBuilder()
def proc = '/bin/cp -rf /var/lib/jenkins/workspace/a-seed-job/job-dsl-scripts/sync-settings/settings.xml /var/lib/jenkins/settings.xml'.execute()
proc.consumeProcessOutput(sout, serr)
proc.waitForOrKill(1000)
println "out> $sout err> $serr"

job("a-create-poll-build-deploy-epic-jobs") {
    parameters {
        stringParam('epic_name', 'epic1')
    }
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
        dsl {
            external('job-dsl-scripts/jenkins-home-depot/aseedjob_create_poll_and_deploy_epic_jobs.groovy')
        }
    }
    publishers {
        logRotator {
            numToKeep(10)
        }
    }
}

job("test-deploy-epic-project") {
    concurrentBuild()
    parameters {
        stringParam('epic_name')
        stringParam('project_name')
    }
    steps {
        shell("deploy-scripts/jenkins/trigger-jenkins-job.sh test-deploy-epic-project $epic_name $project_name")
        shell("deploy-scripts/jenkins/poll-deploy-job-results.sh $epic_name $project_name")
    }
    publishers {
        logRotator {
            numToKeep(10)
        }
    }
}

hd_aem_host = '172.24.103.213'
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

