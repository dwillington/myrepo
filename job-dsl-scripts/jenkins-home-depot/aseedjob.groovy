// credentials
// dwillington-credentials
// axa8962-credentials / qa02test!

// a-seed-job
// https://github.com/dwillington/myrepo.git
// job-dsl-scripts/jenkins-home-depot/**/*.groovy

// JENKINS_JAVA_OPTIONS="-Djava.awt.headless=true -Dhttp.proxyHost=str-www-proxy2-qa.homedepot.com -Dhttp.proxyPort=8080"
// JENKINS_JAVA_OPTIONS="-Dhttp.proxyHost=str-www-proxy2-qa.homedepot.com -Dhttp.proxyPort=8080"

// cp global maven settins.xml (everytime I guess)
// wow major hard coding below!
println "/bin/cp -rf /var/lib/jenkins/workspace/a-seed-job/job-dsl-scripts/sync-settings/settings.xml /var/lib/jenkins/settings.xml".execute().text

job("create-poll-and-build-epic-jobs") {
    parameters {
        stringParam('epic_name')
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

