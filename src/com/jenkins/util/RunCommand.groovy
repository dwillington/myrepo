package com.jenkins.util

import com.jenkins.util.Reporter

public class RunCommand extends com.jenkins.Step {

    public RunCommand(Object scope, String name) {
        this.scope = scope
        this.name = name
    }

    protected void execute(Map config) throws Exception {
        scope.stage(name) {
            scope.dir(currentDirectory+config.workspace) {
                String toolString = setupTools(config)
                scope.withEnv(["PATH+WHATEVER=${toolString}"]) {
                    runOsCMD(scope, config.command)
                }
            }
        }
    }

    protected String report(Map config, String status) throws Exception {
        return Reporter.simpleReport(name, status)
    }

}