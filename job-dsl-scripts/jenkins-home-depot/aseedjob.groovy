// a-seed-job
// https://github.com/dwillington/myrepo.git
// job-dsl-scripts/jenkins-home-depot/**/*.groovy

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

