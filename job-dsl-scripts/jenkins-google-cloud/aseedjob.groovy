// a-seed-job
// https://github.com/dwillington/myrepo.git
// job-dsl-scripts/jenkins-google-cloud/**/*.groovy

job("create-epic-environment") {
    parameters {
        stringParam('epic_name', 'master')
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
        stringParam('epic_name', 'master')
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
        shell('gcloud-scripts/epic-server-urls.sh $epic_name')
    }
    publishers {
        logRotator {
            numToKeep(10)
        }
    }
}

job("create-provision-epic-project") {
    parameters {
        stringParam('epic_name')
        stringParam('project_name')
        stringParam('cpu', '2')
        stringParam('memory', '8')
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
        shell('gcloud-scripts/create-vm.sh $epic_name $project_name $cpu $memory')
        shell('gcloud-scripts/provision-vm.sh $epic_name $project_name')
    }
    publishers {
        logRotator {
            numToKeep(10)
        }
    }
}

job("tear-down-epic-environment") {
    parameters {
        stringParam('epic_name', 'master')
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
    concurrentBuild()
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
        shell('cd deploy-scripts/${project_name}\n' +
              './deploy.sh ${epic_name}'
             )
    }
    publishers {
        logRotator {
            numToKeep(10)
        }
        postBuildTask {
            task('Finished: SUCCESS', 'echo deploy ${epic_name} ${project_name} SUCCESS (${BUILD_NUMBER}) | gsutil cp - gs://np-cadotcom.appspot.com/ci-builds/epic-deploy-results/${epic_name}/${project_name}/deploy.result')
            task('Finished: FAILURE', 'echo deploy ${epic_name} ${project_name} FAILURE (${BUILD_NUMBER}) | gsutil cp - gs://np-cadotcom.appspot.com/ci-builds/epic-deploy-results/${epic_name}/${project_name}/deploy.result')
        }
    }
}

// createPollAndDeployJob('epic1', 'solr')

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