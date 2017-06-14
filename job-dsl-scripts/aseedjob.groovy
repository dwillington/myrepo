job('ASSETMGR') {
    scm {
        rtc {
            configure { node ->
                // node represents <com.ibm.team.build.internal.hjplugin.RTCScm>
				buildTypeStr 'buildStream'
				buildStream 'AssetMgr_Dev'
			}
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