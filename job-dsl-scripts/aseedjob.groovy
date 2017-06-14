job('ASSETMGR') {
    scm {
        rtc {

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