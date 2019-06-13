import groovy.json.*

try {
    node("CBAW-Main") {
        currentBuild.result = 'SUCCESS'
        def scm_list = [$class: 'GitSCM', branches: [[name: env.GIT_BRANCH]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: env.GIT_CLONE_CREDS, url: env.GIT_CLONE_URL]]]
        def uRCfg = new hudson.plugins.git.UserRemoteConfig(env.GIT_CLONE_URL, "", "", env.GIT_CLONE_CREDS)
        def bSpc = new hudson.plugins.git.BranchSpec(env.GIT_BRANCH)
        def scm = new hudson.plugins.git.GitSCM(
            [uRCfg],
            [bSpc],
            false,
            [],
            new hudson.plugins.git.browser.GitWeb(env.GIT_CLONE_URL),
            null,
            []
        )
        stage('Clone') {
           checkout([$class: 'GitSCM', branches: [[name: env.GIT_BRANCH]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CloneOption', timeout: 30]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: env.GIT_CLONE_CREDS, url: env.GIT_CLONE_URL]]])
        }
        stage ('Checkout local branch') {
            sh """
                git checkout -b integrate/${env.BUILD_TAG}
            """
        }
        stage ('Fetch and Integrate Latest Artifact') {
            withEnv(["PATH+WHATEVER=${tool 'MAVEN 3.3.9 Default - Linux'}/bin"]) {
                def groupID="com.td.hoj"
                def artifactID="angular-full-app-R3.1.0"
                def version=params.ANGULAR_VERSION
                def packaging="zip"
                sh """
                    if [ -d target ] ; then
                        rm -rv target
                    fi
                    mkdir target
                    mvn dependency:copy -Dartifact=${groupID}:${artifactID}:${version}:${packaging} -DoutputDirectory=${workspace}/target -B
                    cd target
                    unzip ${artifactID}-*.zip
                    cp -r $WORKSPACE/target/bin/deploy/gulp/.tmp/pages $WORKSPACE/src
                    cp -r $WORKSPACE/target/bin/deploy/gulp/.tmp/staticresources $WORKSPACE/src
                    cd ..
                    rm -rf target
                """
            }
        }
        stage ('Commit Changes') {
            sh """
                #export NAME=\$(find target -name ${artifactID}-*.zip -type f | cut -d/ -f2 | rev | cut -d. -f2- | rev)
                git add -A
                git commit -am 'HOJ-9672 - Integrating ${artifactID} into rcp-salesforce' || true
            """
        }
        stage ('Push Changes') {
            org.jenkinsci.plugins.gitclient.GitClient gitClient = scm.createClient(
                getContext(hudson.model.TaskListener.class),
                currentBuild.getRawBuild().getEnvironment(),
                currentBuild.getRawBuild(),
                getContext(hudson.FilePath.class)
            )
            def branches = new ArrayList<hudson.plugins.git.GitPublisher.BranchToPush>()
            branches.add( new hudson.plugins.git.GitPublisher.BranchToPush("origin", "integrate/${env.BUILD_TAG}") )
            println pushBranches(branches, scm, gitClient, currentBuild.getRawBuild().getEnvironment(), getContext(hudson.model.TaskListener.class), false)
        }
        stage ('Push to Salesforce') {
            sh """
                if [ -d "rcp-salesforce" ]; then
                    rm -rf rcp-salesforce
                fi
                git clone ssh://git@code.td.com/rcp/rcp-salesforce.git
                rm -rf rcp-salesforce/src/pages
                rm -rf rcp-salesforce/src/staticresources
                cp -r $WORKSPACE/src/pages rcp-salesforce/src
                cp -r $WORKSPACE/src/staticresources rcp-salesforce/src
                cd rcp-salesforce
                git add -A
                git commit -am "angular-handoff"
                git pull
                git push origin $env.GIT_BRANCH
                cd ..
                rm -rf rcp-salesforce
            """
            //openPullRequest("integrate/${env.BUILD_TAG}", "${env.GIT_PROJECT}", "${env.GIT_REPO}", "${env.GIT_BRANCH}", "${env.GIT_PROJECT}", "${env.GIT_REPO}", "${env.BUILD_TAG}", "Add latest angular code into repo\\n${env.BUILD_URL}", env.REVIEWERS.split(','))
        }
    }
} catch (e) {
    currentBuild.result="FAILURE"
    println e
} finally {
        
}

int sendBuildStatus(state, key) {
    try {
        commitHash = sh returnStdout: true, script: 'git rev-parse HEAD'
        shortCommitHash = sh returnStdout: true, script: 'git rev-parse --short HEAD'
        def json = new JsonBuilder([
            state: state,
            key: key+"-"+shortCommitHash,
            url: env.BUILD_URL
        ]).toString()
        response = httpRequest authentication: env.GIT_CLONE_CREDS, contentType: 'APPLICATION_JSON', httpMode: 'POST', requestBody: json, url: "https://code.td.com/rest/build-status/1.0/commits/${commitHash}", consoleLogResponseBody: true
        return 1

    } catch (e) {
        println e
        return -1
    }
}

@NonCPS
int openPullRequest(srcBranch, srcProj, srcRepo, destBranch, destProj, destRepo, title, description, reviewers) {
    try {
        def reviewersJSON = ""
        if (reviewers.size() == 0) {
            reviewersJSON = "null"
        } else {
            reviewersJSON="{ \"user\": { \"name\": \"${reviewers[0].toUpperCase()}\" } }"
            for (int i=1; i<reviewers.size(); i++) {
                reviewersJSON+=",{ \"user\": { \"name\": \"${reviewers[i].toUpperCase()}\" } }"
            }
        }
        println "HERE"
        def json = new JsonBuilder()
        message = """
            {
                \"title\": \"${title}\",
                \"description\": \"${description}\",
                \"state\": \"OPEN\",
                \"open\": true,
                \"closed\": false,
                \"fromRef\": {
                    \"id\": \"refs/heads/${srcBranch}\",
                    \"repository\": {
                        \"slug\": \"${srcRepo}\",
                        \"name\": null,
                        \"project\": {
                            \"key\": \"${srcProj}\"
                        }
                    }
                },
                \"toRef\": {
                    \"id\": \"refs/heads/${destBranch}\",
                    \"repository\": {
                        \"slug\": \"${destRepo}\",
                        \"name\": null,
                        \"project\": {
                            \"key\": \"${destProj}\"
                        }
                    }
                },
                \"locked\": false,
                \"reviewers\": [
                    ${reviewersJSON}
                ],
                \"links\": {
                    \"self\": [
                        null
                    ]
                }
            }
        """
        println message
        response = httpRequest authentication: env.GIT_CLONE_CREDS, contentType: 'APPLICATION_JSON', httpMode: 'POST', requestBody: message, url: "https://code.td.com/rest/api/1.0/projects/${destProj}/repos/${destRepo}/pull-requests", consoleLogResponseBody: true
        println response
        return 1
    } catch (e) {
        println e
        return -1        
    }
}

boolean pushBranches(branchesToPush, gitSCM, git, environment, listener, forcePush) {
    for (final hudson.plugins.git.GitPublisher.BranchToPush b : branchesToPush) {
        if (b.getBranchName() == null)
            throw new hudson.AbortException("No branch to push defined");

        if (b.getTargetRepoName() == null)
            throw new hudson.AbortException("No branch repo to push to defined");

        final String branchName = environment.expand(b.getBranchName());
        final String targetRepo = environment.expand(b.getTargetRepoName());
        
        try {
            // Lookup repository with unexpanded name as GitSCM stores them unexpanded
            org.eclipse.jgit.transport.RemoteConfig remote = gitSCM.getRepositoryByName(b.getTargetRepoName());

            if (remote == null)
                throw new hudson.AbortException("No repository found for target repo name " + targetRepo);

            // expand environment variables in remote repository
            remote = gitSCM.getParamExpandedRepo(environment, remote);

            listener.getLogger().println("Pushing HEAD to branch " + branchName + " at repo "
                                         + targetRepo);
            remoteURI = remote.getURIs().get(0);
            org.jenkinsci.plugins.gitclient.PushCommand push = git.push().to(remoteURI).ref("HEAD:" + branchName);
            if (forcePush) {
              push.force();
            }
            push.execute();
        } catch (hudson.plugins.git.GitException e) {
            e.printStackTrace(listener.error("Failed to push branch " + branchName + " to " + targetRepo));
            return false;
        } catch (Exception f) {
            f.printStackTrace(listener.error("Don't know what happend"))
            return false
        }
    }
    return true
}
