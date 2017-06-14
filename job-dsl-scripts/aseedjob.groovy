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
            rootPOM("AssetMgr/pom.xml")
            goals('--batch-mode package')
            mavenInstallation('apache-maven-3.2.1')
            providedGlobalSettings('maven-settings.xml')
            localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
        }

    }
}