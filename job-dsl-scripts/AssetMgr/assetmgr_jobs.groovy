rtc_project_name = 'AssetMgr'
rtc_stream_name = "${rtc_project_name}_Dev"

pipelineJob("${rtc_stream_name}-pipeline") {
    definition {
        // cpsScm {
            // scm {
                // git {
                    // branch('master')
                    // remote {
                        // url('https://dwillington@github.com/dwillington/myrepo.git')
                        // credentials('dwillington@yahoo.com')
                    // }
                // }
            // }
            // scriptPath("pipeline-scripts/${rtc_project_name}/jenkinsfile_dev")
        // }
        cps {
            script(
"""
pipeline {
    agent any
    stages {
        stage('build') {
            steps {
                build "${rtc_stream_name}-build"
            }
        }
        stage('sonar scan') {
            steps {
                echo ''
            }
        }
        stage('fortify scan') {
            steps {
                echo ''
            }
        }
        stage('deploy') {
            steps {
                echo ''
            }
        }
    }
}
""")
            sandbox()
        }
    }
}

job("${rtc_stream_name}-build") {
    parameters {
        stringParam('buildResultUUID')
    }
    // configure { project ->
        // project / scm(class: 'com.ibm.team.build.internal.hjplugin.RTCScm') {
            // overrideGlobal 'false'
            // buildTypeStr 'buildStream'
            // buildStream "${rtc_stream_name}"
        // }
    // }
    scm {
        rtc {
            buildDefinition('AssetMgr_DEV_BD_POC')
        }
    }
    steps {
        maven {
            rootPOM("${rtc_project_name}/pom.xml")
            goals('--batch-mode deploy')
            mavenInstallation('apache-maven-3.2.1')
            providedGlobalSettings('maven-settings.xml')
            localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
            property('altDeploymentRepository', 'maven-repo::default::http://maven-repo.fmr.com:8081/artifactory/libs-snapshot-local/')
        }
    }
    publishers {
        archiveArtifacts {
            pattern('**/target/*.war')
            onlyIfSuccessful()
        }
    }
    logRotator {
        numToKeep(5)
        artifactNumToKeep(1)
    }
}

job("${rtc_stream_name}-deploy") {
    label('docker')
    scm {
        git {
            branch('master')
            remote {
                url('https://dwillington@github.com/dwillington/myrepo.git')
                credentials('dwillington@yahoo.com')
            }
        }
    }
    steps {
        copyArtifacts("${rtc_stream_name}-build") {
            includePatterns('**/*.war')
            buildSelector {
                latestSuccessful(true)
            }
        }
        shell ("" +
               "deploy-scripts/docker/create-tcserver.sh 9080" + "\n" + 
               "deploy-scripts/AssetMgr/bin/deploy.sh 9080" + "\n" + 
               "")
    }
    logRotator {
        numToKeep(5)
    }
}