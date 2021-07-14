package com.jenkins.notify.scm

import com.jenkins.util.Utilities
import groovy.json.*

public class SCMNotification extends com.jenkins.notify.Notifier {

    public SCMNotification(Object scope, String name) {
        this.scope = scope
        this.name = name
    }

    protected void notify(Map config) throws Exception {
        switch(config.pipeline_version) {
            case "v1":
                notifySCM(config)
                break
            case "v2":
                notifySCM(config)
                break
            default:
                status = "FAILURE"
                Utilities.printToConsoleOutput(scope, ["RED", "Invalid pipeline_version provided"])
        }
    }

    protected String report(Map config, String status) throws Exception {
        return ""
    }

    private void notifySCM(Map config) throws Exception {
        scope.stage(name) {
            scope.dir(currentDirectory+config.workspace) {
                if (sendNotification(config)) {
                    String state = null
                    // Map Jenkins build status to Bitbucket build status
                    switch(scope.currentBuild.result) {
                        case "FAILURE":
                            state = "FAILED"
                            break
                        case "ABORTED":
                            state = "STOPPED"
                            break
                        case "SUCCESS":
                            state = "SUCCESSFUL"
                            break
                        case "UNSTABLE":
                            state = "SUCCESSFUL"
                            break
                    }
                    if (config.in_progress) {
                        state = "INPROGRESS"
                    }
                    // Utilities.printToConsoleOutput(scope, [["BLUE", "BitBucket notification state"], ["CYAN", state]], " : ")
                    // String commitHash = scope.sh returnStdout: true, script: 'git rev-parse HEAD'
                    // String shortCommitHash = scope.sh returnStdout: true, script: 'git rev-parse --short HEAD'
                    // def json = new JsonBuilder([
                        // name: "${currentBranch} ${buildNumber}",
                        // state: state,
                        // key: currentBranch + "-" + shortCommitHash,
                        // url: buildUrl
                    // ]).toString()
                    // Utilities.printToConsoleOutput(scope, [["BLUE", "JSON body of request"], ["CYAN", json.toString()]], " : ")
                    // def response = scope.httpRequest authentication: config.notification_scm_account_id, contentType: 'APPLICATION_JSON',
                        // httpMode: 'POST', requestBody: json, url: "https://code.td.com/rest/build-status/1.0/commits/${commitHash}",
                        // consoleLogResponseBody: true
                    // Utilities.printToConsoleOutput(scope, [["BLUE", "response"], ["CYAN", response.toString()]], " : ")
                    // if (response.status == 204) {
                        // Utilities.printToConsoleOutput(scope, ["GREEN", "Successfully notified Bitbucket"])
                    // }
                    // else {
                        // status = "FAILURE"
                        // Utilities.printToConsoleOutput(scope, ["RED", "unexpected http status code : " + response.status.toString()])
                    // }
                }
                else {
                    Utilities.printToConsoleOutput(scope, [["BLUE", "Not sending SCM notification for"],
                        ["CYAN", scope.currentBuild.result]], " : ")
                }
            }
        }
    }

}