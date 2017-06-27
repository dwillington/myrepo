rtc_project_name = 'AssetMgrService'
rtc_stream_name = "${rtc_project_name}_Dev"

pipelineJob("${rtc_stream_name}-pipeline") {
    definition {
        cpsScm {
            scm {
                git {
                    branch('master')
                    remote {
                        url('https://dwillington@github.com/dwillington/myrepo.git')
                        credentials('dwillington@yahoo.com')
                    }
                }
            }
            scriptPath("pipeline-scripts/${rtc_project_name}/jenkinsfile_dev")
        }
        // cps {
            // script(
// """
// """)
            // sandbox()
        // }
    }
    logRotator {
        numToKeep(5)
        artifactNumToKeep(1)
    }
}

job("${rtc_stream_name}-build") {
    configure { project ->
        project / scm(class: 'com.ibm.team.build.internal.hjplugin.RTCScm') {
            overrideGlobal 'false'
            buildTypeStr 'buildStream'
            buildStream "${rtc_stream_name}"
        }
    }
    steps {
        maven {
            rootPOM("AssetMgrServices/pom.xml")
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