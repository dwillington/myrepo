import groovy.json.*

try {
    node("CBAW-Main") {
        currentBuild.result = 'SUCCESS'
		def artifactID=params.ARTIFACT_ID
        stage ('Fetch Latest Artifact') {
            withEnv(["PATH+WHATEVER=${tool 'MAVEN 3.3.9 Default - Linux'}/bin"]) {
                def groupID="com.td.hoj"
                def version=params.ANGULAR_VERSION
                def packaging="zip"
                sh """
					mkdir -p ${artifactID} && cd ${artifactID}
                    rm -rf target && mkdir target && cd target
                    mvn dependency:copy -Dartifact=${groupID}:${artifactID}:${version}:${packaging} -DoutputDirectory=./ -B
                    unzip ${artifactID}-*.zip
					cd $WORKSPACE
                """
            }
        }
        stage ('Integrate Latest Artifact and Push to Salesforce') {
            sh """
				cd ${artifactID}
				#rm -rf rcp-salesforce
				if [ ! -d "rcp-salesforce" ]; then
					# time git clone ssh://git@code.td.com/rcp/rcp-salesforce.git
					time git clone --branch $env.GIT_BRANCH --depth 1 ssh://git@code.td.com/rcp/rcp-salesforce.git
				fi
                cd rcp-salesforce
				git checkout $env.GIT_BRANCH
				git reset --hard origin/$env.GIT_BRANCH
				git pull
				if [ -d "$WORKSPACE/${artifactID}/target/.tmp" ]; then
					# rcp-mortgage-preapproval-client
					cp -r $WORKSPACE/${artifactID}/target/.tmp/pages src
					cp -r $WORKSPACE/${artifactID}/target/.tmp/staticresources src
				else
					# angular-full-app
					cp -r $WORKSPACE/${artifactID}/target/bin/deploy/gulp/.tmp/pages src
					cp -r $WORKSPACE/${artifactID}/target/bin/deploy/gulp/.tmp/staticresources src
				fi
				git pull
                git add -A
                git commit -am "angular-handoff"
                git push origin HEAD:$env.GIT_BRANCH --force
            """
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