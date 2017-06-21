rtc_project_name = 'AssetMgr'
rtc_stream_name = "${rtc_project_name}_Dev"

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
            pattern('**/*.war')
            onlyIfSuccessful()
        }
    }
    logRotator {
        numToKeep(5)
        artifactNumToKeep(1)
    }
}

job("${rtc_stream_name}-deploy") {
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
               "" + "\n" + 
               "" + "\n" + 
               "" + "\n" + 
               "" + "\n" + 
               "")
    }
    logRotator {
        numToKeep(5)
    }
}