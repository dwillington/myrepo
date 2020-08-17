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
            stage('checkout') {
                steps {
                    checkout scm: [$class: 'MercurialSCM', source: pipelineParams.scmUrl, revisionType: 'BRANCH', revision: pipelineParams.branch, credentialsId: 'jenkins-rhodecode']
                            // git branch: pipelineParams.branch, url: pipelineParams.scmUrl
                    }
            }
            stage('build') {
                steps {
                    withMaven(maven:'mvn-3.5.3', jdk: 'jdk-9.0.4') { //, globalMavenSettingsConfig: 'maven-settings.xml'
                        sh 'mvn clean package -DskipTests=true'
                        // sh 'mvn --version'
                    }
                }
            }
            stage ('unit test') {
                steps {
                    withMaven(maven:'mvn-3.5.3', jdk: 'jdk-9.0.4') {
                        sh 'mvn test'
                    }
                }
            }
            stage('sonar scan') {
                steps {
                    withSonarQubeEnv('sonarqube') {
                        withMaven(maven:'mvn-3.5.3', jdk: 'jdk-9.0.4') { //, globalMavenSettingsConfig: 'maven-settings.xml'
                            sh 'mvn org.sonarsource.scanner.maven:/-maven-plugin:3.2:sonar'
                        }
                    }
                }
            }
            stage('static security scan') {
                steps {
                    sh "echo fority.sh"
                }
            }
            stage('publish to repository') {
                steps {
                    withMaven(maven:'mvn-3.5.3', jdk: 'jdk-9.0.4', globalMavenSettingsConfig: 'settings.xml') {
                        sh "mvn deploy"
                    }
                }
            }
        }
    }
}
