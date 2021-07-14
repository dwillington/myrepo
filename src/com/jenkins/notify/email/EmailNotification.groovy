package com.jenkins.notify.email

import com.jenkins.util.Utilities

public class EmailNotification extends com.jenkins.notify.Notifier {

    public EmailNotification(Object scope, String name) {
        this.scope = scope
        this.name = name
    }

    protected void notify(Map config) throws Exception {
        switch(config.pipeline_version) {
            case "v1":
                sendEmail(config)
                break
            case "v2":
                sendEmail(config)
                break
            default:
                status = "FAILURE"
                Utilities.printToConsoleOutput(scope, ["RED", "Invalid pipeline_version provided"])
        }
    }

    protected String report(Map config, String status) throws Exception {
        return ""
    }

    private void sendEmail(Map config) throws Exception {
        scope.stage(name) {
            scope.dir(currentDirectory+config.workspace) {
				if(true) {
				}
                else if (sendNotification(config)) {
                    String emailReport = null
                    String duration = ""
                    if (config.email_type == "CONSOLIDATED") {
                        // If the report file exists, remove it in order not to conflict with the consolidated report
                        String command = """
                            rm -f $config.report_file || true
                        """
                        runOsCMD(scope, command)
                        // Initialize the consolidated report file
                        scope.writeFile file: "consolidated_report", text: "<!--START-->"
                        scope.unstash name: "build_info"
                        String buildInfo = scope.readFile "build_info"
                        config.group_id = config.group_id + "." + buildInfo
                        // For all branches, append report for each branch into the consolidated report
                        for (branch in config.all_branches) {
                            config.artifact_id = branch
                            downloadArtifactFromNexus(config)
                            String consolidatedReport = scope.readFile file: "consolidated_report"
                            String report = scope.readFile file: branch
                            scope.writeFile file: "consolidated_report", text: consolidatedReport + report + "<br/>"
                        }
                        Integer timeInSec = 0
                        // For the standard branches, add up the time each branch takes to obtain the total time
                        for (branch in config.time_report_branches) {
                            String content = scope.readFile file: branch
                            String time = content.split("Build time on $branch: ")[1].replace("</h1>", "").replace("<br/>", "")
                            switch(time.split(" ").size()) {
                                case "6": // greater than hour
                                    timeInSec += time.split(" ")[0].toInteger() * 3600 + time.split(" ")[2].toInteger() * 60 +
                                        time.split(" ")[4].toInteger()
                                    break
                                case "4": // greater than minute
                                    timeInSec += time.split(" ")[0].toInteger() * 60 + time.split(" ")[2].toInteger()
                                    break
                                case "2": // less than minute
                                    timeInSec += time.split(" ")[0].toInteger()
                                    break
                                default:
                                    break
                            }
                        }
                        int hours = timeInSec / (int) 3600
                        timeInSec %= (int) 3600
                        int minutes = timeInSec / (int) 60
                        timeInSec %= (int) 60
                        int seconds = (int) timeInSec
                        if (hours > 0) {
                            duration += "${hours} hr(s) ${minutes} mins ${seconds} secs"
                        } else {
                            duration += "${minutes} mins ${seconds} secs"
                        }
                        Utilities.printToConsoleOutput(scope, ["BLUE", "Total time taken: " + duration])
                        // Add the total time to the email report
                        String existingReport = scope.readFile file: "consolidated_report"
                        existingReport += "<h1 style='background-color:yellow; display:inline-block; font-size:14px'>Total time taken:  " +
                            duration + "</h1><br/>"
                        scope.writeFile file: "consolidated_report", text: existingReport + "<br/>"
                        config.artifact_id = "consolidated"
                        config.filename = "consolidated_report"
                        // Store the consolidated report in Nexus
                        publishArtifactToNexus(config)
                        emailReport = scope.readFile file: "consolidated_report"
                    }
                    // else {
                        // Utilities.printToConsoleOutput(scope, ["RED", "Cannot determine email type"])
                    // }
                    String subject = getSubject(config)
                    scope.mail bcc: '', body: emailReport, cc: '', from: config.notification_email_sender, replyTo: '', subject: subject,
                        to: config.notification_email_recipients, mimeType: 'text/html'
                }
                else {
                    Utilities.printToConsoleOutput(scope, [["BLUE", "Not sending Email notification for"],
                        ["CYAN", scope.currentBuild.result]], " : ")
                }
            }
        }
    }

    private String getSubject(Map config) throws Exception {
        def priorStatus = scope.currentBuild.getPreviousBuild()?.getResult()
        def currentStatus = scope.currentBuild.result

        String status = null
        if (config.email_type == "REGULAR") {
            if (currentStatus == "SUCCESS" && priorStatus != "SUCCESS") {
                status = "FIXED"
            } else if (priorStatus == "SUCCESS" && currentStatus != "SUCCESS") {
                status = 'BROKEN'
            } else {
                status = scope.currentBuild.result
            }
        }

        else if (config.email_type == "CONSOLIDATED") {
            status = "Overall Pipeline Workflow"
        }

        return config.mal_code + " => " + status
    }
}