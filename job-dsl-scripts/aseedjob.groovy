


job("create-epic-environment") {
    parameters {
        stringParam('epic_name')
    }
    scm {
        git {
            remote {
                url("https://github.com/dwillington/myrepo.git")
                credentials('1a678709-ecf9-42a6-b1ea-82acbcab44bb')
                branch('gcloud')
            }
        }
    }
    steps {
        shell('gcloud-scripts/create-epic-environment.sh $epic_name')
    }
    publishers {
        logRotator {
            numToKeep(10)
        }
    }
}
