


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

job("provision-epic-environment") {
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
        shell('gcloud-scripts/provision-epic-environment.sh $epic_name')
    }
    publishers {
        logRotator {
            numToKeep(10)
        }
    }
}

job("tear-down-epic-environment") {
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
        shell('gcloud-scripts/tear-down-epic-environment.sh $epic_name')
    }
    publishers {
        logRotator {
            numToKeep(10)
        }
    }
}

job("poll-and-deploy-epic1-solr") {
    scm {
        git {
            remote {
                url("https://github.com/dwillington/myrepo.git")
                credentials('1a678709-ecf9-42a6-b1ea-82acbcab44bb')
                branch('gcloud')
            }
        }
    }
    configure { project ->
        project / triggers / "org.jenkinsci.plugins.fstrigger.triggers.FolderContentTrigger" (plugin: "fstrigger@0.39") {
            spec("* * * * *")
            path('/mnt/gcs-bucket/ci-builds/epic-builds/epic1/solr')
            excludeCheckLastModificationDate('false')
            excludeCheckContent('true')
            excludeCheckFewerOrMoreFiles('false')
        }
    }
    steps {
        shell('deploy-scripts/solr/deploy.sh epic1-solr')
    }
    publishers {
        logRotator {
            numToKeep(10)
        }
    }
}
