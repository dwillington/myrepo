def call(body) {
    // evaluate the body block, and collect configuration into the object
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {
        // agent any
        agent { label 'master' }
        stages {
            stage('git checkout') {
                steps {
                    git branch: pipelineParams.branch, url: pipelineParams.scmUrl
                }
            }
            stage('prepare database') {
                steps {
                    sh "echo prepare database"
                }
            }
            stage('deploy') {
                steps {
                    copyArtifacts filter: '**/hello-world-war-*.war', projectName: 'devops-war-build', target: 'target', flatten: 'true'
                    sh "ls -al target"
                    sh """
                        if [[ -e bin/stage.sh ]] 
                        then
                            ./bin/stage.sh
                        fi
                       """
                }
            }
            stage('selenium integration testing') {
                steps {
                    sh "echo selenium integration testing"
                }
            }
        }
    }
}
