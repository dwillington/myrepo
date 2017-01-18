job("create-epic-environment") {
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
                credentials('dwillington-credentials')
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
                credentials('dwillington-credentials')
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

job("deploy-epic-project") {
    parameters {
        stringParam('epic_name')
        stringParam('project_name')
    }
    authenticationToken('password')
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
        shell('deploy-scripts/${project_name}/deploy.sh ${epic_name}')
    }
    publishers {
        logRotator {
            numToKeep(10)
        }
    }
}

createPollAndDeployJob('epic1', 'solr')

def createPollAndDeployJob(epic_name, project_name) {
    job("poll-and-deploy-${epic_name}-${project_name}") {
        scm {
            git {
                remote {
                    url("https://github.com/dwillington/myrepo.git")
                    credentials('dwillington-credentials')
                    branch('gcloud')
                }
            }
        }
        configure { project ->
            project / triggers / "org.jenkinsci.plugins.fstrigger.triggers.FolderContentTrigger" (plugin: "fstrigger@0.39") {
                spec("* * * * *")
                path("/mnt/gcs-bucket/ci-builds/epic-builds/${epic_name}/${project_name}")
                excludeCheckLastModificationDate('false')
                excludeCheckContent('true')
                excludeCheckFewerOrMoreFiles('false')
            }
        }
        steps {
            shell("deploy-scripts/${project_name}/deploy.sh epic1")
        }
        publishers {
            logRotator {
                numToKeep(10)
            }
        }
    }
}