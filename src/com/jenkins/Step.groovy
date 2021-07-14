package com.jenkins

import com.jenkins.util.Utilities

public abstract class Step implements Serializable {

    protected Object scope
    protected String name
    protected String status
    protected String report
    protected String currentDirectory
    protected String currentBranch
    protected String buildNumber
    protected String buildTag
    protected String buildUrl
    protected String jobUrl

    protected abstract String report(Map config, String status)
    protected abstract void execute(Map config)

    // This method is called from scipts inside "vars" folder of multibranch pipeline
    protected void run(Map config) {
        try {
            setEnvironmentVariables(config)
            status = checkoutSCM(config)
            // If "ABORTED" states, we don't want to execute the code
            // Keep in mind there are cases we want to execute the code even when the status is "FAILURE"
            if (status != "ABORTED") {
                execute(config)
            }
            else {
                Utilities.printToConsoleOutput(scope, [["BLUE", "Skipping"], ["YELLOW", config.stage_name],
                    ["BLUE", "due to previous step aborting"]], " ")
            }
        }
        catch (e) {
            status = "FAILURE"
            Utilities.printToConsoleOutput(scope, ["RED", e.toString()])
        }
        finally {
            report = report(config, status)
            cleanUpWorkspace(config)
            return status
        }
    }

    // This is to conveniently refer to environment variables from other classes
    protected void setEnvironmentVariables(Map config) throws Exception {
        Utilities.printToConsoleOutput(scope, ["BLUE", "Setting up environment variables"])
        this.currentDirectory = scope.env.WORKSPACE
        this.currentBranch = scope.env.BRANCH_NAME
        this.buildNumber = scope.env.BUILD_NUMBER
        this.buildTag = scope.env.BUILD_TAG
        this.buildUrl = scope.env.BUILD_URL
        this.jobUrl = scope.env.JOB_URL
    }

    // This method checks out the code and sets up reporting file
    protected String checkoutSCM(Map config) throws Exception {
        if (config.checkout_scm) {
            scope.stage(config.git_clone_stage_name) {
                Utilities.printToConsoleOutput(scope, ["BLUE", "Checking out scm"])
                
				// scope.echo "config.scm_url:" + config.scm_url
				// scope.echo "config.scm_branch:" + config.scm_branch

				scope.git url: config.scm_url, branch: config.scm_branch

				// scope.checkout(scope.scm)
				// scope.git url: scope.scm.remote, branch: scope.scm.branch
				// scope.git([url: config.scm_url, branch: config.scm_branch])
				// hudson.AbortException: ‘checkout scm’ is only available when using “Multibranch Pipeline” or “Pipeline script from SCM”

				// scope.git([url: 'https://github.com/dwillington/hello-world-war.git', branch: 'master'])

                // getSourceBranchInfo(config)
                // Utilities.printToConsoleOutput(scope, ["BLUE", "Setting up report"])
                // if (isLastCommitterJenkins(scope, config)) {
                    // return "ABORTED"
                // }
                // else {
				return "SUCCESS"
                // }
            }
        }
        else {
            Utilities.printToConsoleOutput(scope, ["BLUE", "Not checking out scm"])
            return "SUCCESS"
        }
    }

    // Based on the configuration, this method will be used to clean up the workspace
    // Notice calling this method is redundant when builds are taking place on container images
    protected void cleanUpWorkspace(Map config) throws Exception {
        if (config.clean_up_workspace) {
            Utilities.printToConsoleOutput(scope, [["BLUE", "Cleaning up workspace"], ["CYAN", currentDirectory]], " : ")
            scope.dir(currentDirectory) {
                String command = """
                    rm -rf *
                    rm -rf .* || true
                """
                runOsCMD(scope, command)
            }
        }
        else {
            Utilities.printToConsoleOutput(scope, ["BLUE", "Continue using the workspace. Not cleaning up"])
        }
    }

    protected String setupTools(Map config) throws Exception {
        def tools = config.tools
        def toolSize = tools.size()
        def toolString = ""

        if (toolSize == 0) {
            return ""
        }
        else {
            toolString += scope.tool(tools[0]) + "/bin"
        }
        for (int index=1; index<toolSize; index++) {
            toolString += ":" + scope.tool(tools[index]) + "/bin"
        }
        Utilities.printToConsoleOutput(scope, [["BLUE", "Tools"], ["CYAN", toolString]], " : ")
        return toolString
    }

    protected void runOsCMD(Object scope, String command) throws Exception {
        Utilities.printToConsoleOutput(scope, ["MAGENTA", command])
        if (scope.isUnix()) {
            scope.sh(command)
        }
        else {
            scope.bat(command)
        }
    }

    // The following 3 methods are very useful when parseing pom.xml file for maven projects
    protected String getGroupId(Map config, String xml) {
        Utilities.printToConsoleOutput(scope, ["BLUE", "Reading group id"])
        def file = scope.readFile xml
        def pom = new XmlParser().parseText(file)
        def group = pom.'groupId'.text()
        return group
    }

    protected String getArtifactId(Map config, String xml) {
        Utilities.printToConsoleOutput(scope, ["BLUE", "Reading artifact id"])
        def file = scope.readFile xml
        def pom = new XmlParser().parseText(file)
        def artifact = pom.'artifactId'.text()
        return artifact
    }

    protected String getCurrentVersion(Map config, String xml) {
        Utilities.printToConsoleOutput(scope, ["BLUE", "Reading current version"])
        def file = scope.readFile xml
        def pom = new XmlParser().parseText(file)
        def version = pom.'version'.text()
        version = version.split('-')[0]
        return version
    }

    // This method is to update the host key file on Jenkins agent when the target linux VM is torn down and re-deployed
    protected void replaceHostKey(Map config) throws Exception {
        String command = """
            ssh-keygen -R $config.deployment_target || true
            ssh-keyscan -H $config.deployment_target >> ~/.ssh/known_hosts
        """
        runOsCMD(scope, command)
    }

    protected void downloadArtifactFromNexus(Map config) throws Exception {
        String command = """
            mvn dependency:copy -Dartifact=${config.group_id}:${config.artifact_id}:${config.version}:${config.packaging} \
                -DrepositoryId=${config.repository} -DoutputDirectory=${currentDirectory} -B
        """
        scope.withMaven(maven: config.maven, globalMavenSettingsConfig: config.global_maven_setting) {
            runOsCMD(scope, command)
        }
        switch(config.packaging) {
            case "tar.gz":
                command = "tar -xzf ${config.artifact_id}-*.tar.gz"
                runOsCMD(scope, command)
                break
            case "zip":
                command = "unzip ${config.artifact_id}-*.zip"
                runOsCMD(scope, command)
                break
            case "html":
                command = """
                    for file in *.html; do
                        mv \$file $config.artifact_id
                    done
                """
                runOsCMD(scope, command)
                break
            default:
                status = "FAILURE"
                Utilities.printToConsoleOutput(scope, ["RED", "Cannot recognize packaging type"])
                break
        }
    }

    protected void publishArtifactToNexus(Map config) throws Exception {
        String command = """
            mvn deploy:deploy-file -DgroupId=${config.group_id} -DartifactId=${config.artifact_id} -DuniqueVersion=false \
                -Dpackaging=${config.packaging} -DrepositoryId=${config.repository} -Durl=${config.nexus_url}/${config.repository} \
                -Dversion=${config.version} -Dfile=${config.filename} -B
        """
        scope.withMaven(maven: config.maven, globalMavenSettingsConfig: config.global_maven_setting) {
            runOsCMD(scope, command)
        }
    }

    // This method extracts string after the slash of the full name of branch used for feature
    // It assume the "standard git flow" model and its naming conventions
    protected void getSourceBranchInfo(Map config) throws Exception {
        String buildInfo = null
        if (config.additional_branches.contains(currentBranch.split("/")[0])) {
            if (currentBranch.split("/").size() == 2) {
                scope.writeFile file: "build_info", text: currentBranch.split("/")[1]
                scope.stash includes: "build_info", name: "build_info"
                buildInfo = currentBranch.split("/")[1]
            }
            else {
                Utilities.printToConsoleOutput(scope, [["RED", "FATAL: Improper branch name"], ["WHITE", currentBranch]], " : ")
            }
        }
        else {
            String lastCommit = scope.sh returnStdout: true, script: 'git log -1 --oneline'
            lastCommit.split(" ").each { string ->
                if (string.contains(config.feature_branch_name_pattern+"/") || string.contains(config.release_branch_name_pattern+"/")) {
                    scope.writeFile file: "build_info", text: string.split("/")[1]
                    scope.stash includes: "build_info", name: "build_info"
                    buildInfo = string.split("/")[1]
                }
            }
        }
        if (buildInfo == null) {
            status = "ABORTED"
        }
        Utilities.printToConsoleOutput(scope, [["BLUE", "Build Info"], ["WHITE", buildInfo]], " : ")
    }

    // There are circumstances where the build has to abort
    protected boolean isLastCommitterJenkins(Object scope, Map config) {
        String lastCommit = scope.sh returnStdout: true, script: 'git log -1 --oneline'
        Utilities.printToConsoleOutput(scope, [["BLUE", "Latest commit"], ["CYAN", lastCommit]], " : ")

        // If current branch is being used for release and and latest commit contains those of maven release plugin
        if ((lastCommit.contains("[maven-release]") || lastCommit.contains("[maven-release-plugin]"))
            && currentBranch.contains(config.release_branch_name_pattern)) {
            Utilities.printToConsoleOutput(scope, ["RED", "Maven release detected, not building"])
            return true
        }
        // If current branch is being used for release and and latest commit contains "certain pattern"
        else if (lastCommit.contains("[npm-release]")) {
            Utilities.printToConsoleOutput(scope, ["RED", "npm release detected, not building"])
            return true
        }
        // If current branch is being used for release and merge in coming from branch used for release
        else if (lastCommit.contains("Merge pull request") && lastCommit.contains("from release")
            && currentBranch.contains(config.develop_branch_name_pattern)) {
            Utilities.printToConsoleOutput(scope, ["RED", "Merge coming from release branch, not building"])
            return true
        }
        else {
            Utilities.printToConsoleOutput(scope, ["GREEN", "Last commit is not from a release. Continue executing pipeline"])
            return false
        }
    }
}