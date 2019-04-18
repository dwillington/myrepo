def call(body) {
    // evaluate the body block, and collect configuration into the object
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {
		agent any
        // agent { label 'docker-agent' }
        stages {
            stage('git checkout') {
                steps {
                    git branch: pipelineParams.branch, url: pipelineParams.scmUrl
                }
            }
            stage('prepare database') {
                steps {
					sh "echo empty"
                }
            }
            stage('deploy') {
                steps {
					step([  $class: 'CopyArtifact',
							filter: 'hellow-world-war-*.war',
							projectName: 'hellow-world-war-build',
							target: 'target'
					])
					// copyArtifacts filter: 'hellow-world-war-*.war', projectName: 'hellow-world-war-build', target: 'target'
					sh "ls -al target"

					// sh """
						// if [[ -e bin/stage.sh ]] 
						// then
							// ./bin/stage.sh
						// fi
					   // """
                }
            }
            stage('acceptance-testing') {
                steps {
					sh "echo empty"
                }
            }
		}
    }
}
