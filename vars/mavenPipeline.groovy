def call(body) {
    // evaluate the body block, and collect configuration into the object
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {
		//agent any
        agent { label 'docker-agent' }
        stages {
            stage('git checkout') {
                steps {
                    git branch: pipelineParams.branch, url: pipelineParams.scmUrl
                }
            }
            stage('build') {
                steps {
					withMaven(maven:'mvn-3.5.3', jdk: 'jdk-9.0.4') { //, globalMavenSettingsConfig: 'maven-settings.xml'
						sh 'mvn clean package -DskipTests=true -Dmaven.compiler.source=1.6 -Dmaven.compiler.target=1.6'
					}
				}
            }
            stage ('unit test') {
                steps {
					withMaven(maven:'mvn-3.5.3', jdk: 'jdk-9.0.4') {
                        sh 'mvn test '
					}
                }
            }
            stage('sonar scan') {
                steps {
                }
            }
            stage('static security scan') {
                steps {
                }
            }
            stage('publish to repository') {
                steps {
                }
            }
            stage('deploy') {
                steps {
                }
            }
            stage('acceptance-testing') {
                steps {
                }
            }
		}
    }
}
