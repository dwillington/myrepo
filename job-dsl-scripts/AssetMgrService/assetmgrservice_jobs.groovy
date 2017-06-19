rtc_project_name = 'AssetMgrService'
rtc_stream_name = "${rtc_project_name}_Dev"

job("${rtc_stream_name}") {
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
    logRotator {
        numToKeep(5)
    }
}