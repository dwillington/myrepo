def call(body) {
    // evaluate the body block, and collect configuration into the object
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {
		//agent any
		agent {
			label 'docker-agent' 
		}
        stages {
            stage('checkout git') {
                steps {
                    git branch: pipelineParams.branch, url: pipelineParams.scmUrl
                }
            }
            stage('build') {
                steps {
					withMaven(maven:'mvn-3.5.3') { //, jdk: 'jdk-10.0.2'
						sh 'mvn clean package -DskipTests=true -Dmaven.compiler.source=1.6 -Dmaven.compiler.target=1.6'
					}
				}
            }
            stage ('test') {
                steps {
					withMaven(maven:'mvn-3.5.3') { //, jdk: 'jdk-10.0.2'
                        sh 'mvn test '
					}
                }
            }
		}
    }
}
