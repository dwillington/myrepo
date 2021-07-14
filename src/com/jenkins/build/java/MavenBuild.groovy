package com.jenkins.build.java

import com.jenkins.util.Utilities

public class MavenBuild extends com.jenkins.build.Builder {

    public MavenBuild(Object scope, String name) {
        this.scope = scope
        this.name = name
    }

    protected void build(Map config) throws Exception {
        switch(config.pipeline_version) {
            case "v1":
                buildWithEnv(config)
                break
            case "v2":
                buildWithMaven(config)
                break
            default:
                status = "FAILURE"
                Utilities.printToConsoleOutput(scope, ["RED", "Invalide pipeline_version provided"])
        }
    }

    protected String report(Map config, String status) {
        // return Reporter.simpleReport(name, status)
    }

    // Build based on settings.xml residing on .m2 folder of Jenkins agent
    private void buildWithEnv(Map config) throws Exception {
        scope.stage(name) {
            scope.dir(currentDirectory+config.workspace) {
                String toolString = setupTools(config)
                scope.withEnv(["PATH+WHATEVER=${toolString}"]) {
                    runOsCMD(scope, "${config.maven_command}")
                }
            }
        }
    }

    // Build based on settings.xml stored as config file on Jenkins master
    private void buildWithMaven(Map config) throws Exception {
        scope.stage(name) {
            scope.dir(currentDirectory+config.workspace) {
			    String toolString = setupTools(config)
				scope.withEnv(["PATH+WHATEVER=${toolString}"]) {
					scope.withMaven(maven: config.maven, globalMavenSettingsConfig: config.global_maven_setting) {
						runOsCMD(scope, "${config.maven_command}")
					}
				}
            }
        }
    }

}