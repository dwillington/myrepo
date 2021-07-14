package com.jenkins.util

import com.jenkins.util.Utilities

@Singleton
public class Reporter {

    public static final ERROR = "tg-error"
    public static final WARN = "tg-warn"
    public static final SUCCESS = "tg-pass"
    public static final ABORTED = "tg-normal"
    public static final STYLING = """
        <style type='text/css'>
            table{border-spacing: 0;border-collapse:collapse;}
            td{font-family:Arial, sans-serif;font-size:14px;border-bottom: 1px solid black; padding:8px;}
            th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;border-bottom: 2px solid black; text-align: left; padding: 8px}
            .tg-error{font-weight:bold;background-color:#EB2B38;color:#ffffff;vertical-align:top}
            .tg-pass-url{background-color:#47947C;a:link:#ffffff;vertical-align:top}
            .tg-warn-url{background-color:#FB8E10;a:link:#ffffff;vertical-align:top}
            .tg-meat{text-align:right;vertical-align:top}
            .tg-title{font-weight:bold;vertical-align:top; background-color:#EBEBEB}
            .tg-normal{vertical-align:top; background-color:#EBEBEB}
            .tg-error-url{background-color:#EB2B38;a:link:#ffffff;vertical-align:top}
            .tg-warn{font-weight:bold;background-color:#FB8E10;color:#ffffff;vertical-align:top}
            .tg-meat-title{font-style:italic;text-align:right;vertical-align:top}
            .tg-pass{font-weight:bold;background-color:#47947C;color:#ffffff;vertical-align:top}
            .tg-normal-title{font-style:italic;vertical-align:top}
        </style>
    """

    public static void setupReporting(Object scope, Map config) {
        scope.writeFile file: config.report_file, text: ""
        scope.stash includes: config.report_file, name: java.net.URLDecoder.decode(scope.env.BUILD_TAG).replace('/', '-')
    }

    public static String openReport(Object scope, Map config) {
        String email_body  = this.STYLING

        email_body += "<table style='border-collapse:collapse; undefined; width: 694px'>" +
            "<colgroup>" +
            "<col style='width: 199px'>" +
            "<col style='width: 151px'>" +
            "<col style='width: 102px'>" +
            "<col style='width: 77px'>" +
            "<col style='width: 91px'>" +
            "<col style='width: 74px'>" +
            "</colgroup>" +
            "<tr>" +
            "<th class='tg-title'>JENKINS</th>" +
            "<th class='tg-title'>"+scope.currentBuild.result+"</th>" +
            "<th class='tg-normal' colspan='4'><a href=\"${scope.env.BUILD_URL}\">Build "+scope.currentBuild.getNumber() +
            " on branch "+java.net.URLDecoder.decode(scope.currentBuild.getProjectName(), "UTF-8")+"</a></th>" +
            "</tr>"
        return email_body
    }

    public static String closeReport() {
        return "</table>"
    }

    public static void addToReport(Object scope, Map config, String report, String agent) {
        scope.unstash name: java.net.URLDecoder.decode(scope.env.BUILD_TAG).replace('/', '-')
        String appender = scope.readFile file: config.report_file
        appender += report
        scope.writeFile file: config.report_file, text: appender
        scope.stash includes: config.report_file, name: java.net.URLDecoder.decode(scope.env.BUILD_TAG).replace('/', '-')
    }

    public static String consolidateReport(Object scope, Map config) {
        scope.unstash name: java.net.URLDecoder.decode(scope.env.BUILD_TAG).replace('/', '-')
        String report = this.openReport(scope, config)
        report += scope.readFile(config.report_file)
        report += this.closeReport()
        String timeString = "<h1 style='background-color:yellow; display:inline-block; font-size:14px'>Build time on " +
            scope.env.BRANCH_NAME.split("/")[0] + ": " + Utilities.endTime + "</h1><br/>"
        report += timeString
        scope.stash includes: config.report_file, name: java.net.URLDecoder.decode(scope.env.BUILD_TAG).replace('/', '-')
        return report
    }

    public static String simpleReport(String stepName, String status) {
        String styling = null
        switch(status) {
            case "SUCCESS":
                styling = this.SUCCESS
                break
            case "UNSTABLE":
                styling = this.WARN
                break
            case "FAILURE":
                styling = this.ERROR
                break
            case "ABORTED":
                styling = this.ABORTED
                break
        }
        String email_body = "<th class='${styling}'>${stepName}</th>" +
                            "<th class='${styling}'>${status}</th>" +
                            "<th class='${styling}' colspan='4'></th>" +
                            "</tr>"
        return email_body
    }

}