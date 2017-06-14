job('AssetMgr') {
    configure { project ->
        project / scm(class: 'com.ibm.team.build.internal.hjplugin.RTCScm') {
            overrideGlobal 'false'
            buildTypeStr 'buildStream'
            buildStream 'AssetMgr_Dev'
        }
    }
    steps {
        maven {
            goals('package')
            localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
            mavenInstallation('apache-maven-3.2.1')
            providedGlobalSettings('maven-settings.xml')
        }

    }
}