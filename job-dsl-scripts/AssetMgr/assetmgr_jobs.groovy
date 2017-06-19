rtc_project_name = 'AssetMgr'
rtc_stream_name = "${rtc_project_name}_Dev"

job("{rtc_project_name}") {
    configure { project ->
        project / scm(class: 'com.ibm.team.build.internal.hjplugin.RTCScm') {
            overrideGlobal 'false'
            buildTypeStr 'buildStream'
            buildStream "{rtc_project_name}"
        }
    }
    steps {
        maven {
            rootPOM("{rtc_project_name}/pom.xml")
            goals('--batch-mode package')
            mavenInstallation('apache-maven-3.2.1')
            providedGlobalSettings('maven-settings.xml')
            localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
        }
    }
    logRotator {
        numToKeep(5)
    }
}