job('ASSETMGR') {
    scm {
        rtc {
            buildDefinition('AssetMgr_Jenkins_BD_ibmdvl11_DEV')
        }
    }
    steps {
		maven {
            goals('package')
            localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
            mavenInstallation('apache-maven-3.2.1')
            providedGlobalSettings('nexus-settings.xml')
        }

    }
}