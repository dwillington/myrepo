node {
    wrap([$class: 'AnsiColorBuildWrapper', colorMapName: "xterm"]) {
        ansibleTower(
            towerServer: 'ansible-tower',
            jobTemplate: 'ansible-host Job Template',
            importTowerLogs: true,
            inventory: 'ansible-host inventory',
            jobTags: '',
            limit: '',
            removeColor: false,
            verbose: true,
            credential: '',
            extraVars: '''---
            my_var: "Jenkins Test"'''
        )
    }
}
node {
    wrap([$class: 'AnsiColorBuildWrapper', colorMapName: "xterm"]) {
        ansibleTower(
            towerServer: 'ansible-tower',
            jobTemplate: 'Application Deploy Job Template',
            inventory: 'JBoss DEV Inventory',
            credential: 'JBoss DEV Credentials',
            importTowerLogs: true,
            limit: '',
            removeColor: false,
            verbose: true,
            extraVars: '''---
            my_var: "Jenkins Test"'''
        )
    }
}
----------------------------------------------------------------------
node ('HOJ-LINUX-AGENT2') {
	stage('git') {
		git branch: 'devops-test',
			credentialsId: 'CBDC-BITBUCKET-PIPELINE-SSHKEY',
			url: 'ssh://git@code.td.com/hoj/angular-full-app.git'      
	}
	stage('npm install') {
		withEnv(["PATH+NPM=${tool 'npm_8_rhel6'}/bin"]) {
			sh "npm install"
		}
	}
	stage('npm run deploy:prod') {
		withEnv(["PATH+NPM=${tool 'npm_8_rhel6'}/bin"]) {
		    sh "echo '{\"prod\": {\"username\": \"\$SF_USER\",\"password\": \"\$SF_PW\$SF_TOKEN\",\"loginUrl\": \"\$SF_URL\"}}' > bin/deploy/credentials.json"
		    sh "cat bin/deploy/credentials.json"
			sh "npm run deploy:prod"
		}
	}
}
----------------------------------------------------------------------
import io.jenkins.docker.connector.*
dockerNode(dockerHost: 'tcp://rhel-2k4p4a.dev.tdct.cloud.td.com:4243', 
		   image: 'jenkinsci/slave-td', 
		   remoteFs: '/home/jenkins', 
		   connector: [$class: 'io.jenkins.docker.connector.DockerComputerAttachConnector', user: 'jenkins'])
	{
	stage('git') {
		git branch: 'devops-test',
			credentialsId: 'CBDC-BITBUCKET-PIPELINE-SSHKEY',
			url: 'ssh://git@code.td.com/hoj/angular-full-app.git'      
	}
	stage('npm install') {
		withEnv(["PATH+NPM=${tool 'npm_8_rhel6'}/bin"]) {
			sh "npm install"
			withSonarQubeEnv('CBAW-SonarQube') {
				withEnv(["PATH+SONAR=${tool 'sonar-scanner_3.0.3_rhel6'}/bin"]) {
					sh "sonar-scanner"
				}
			}
		}
	}
	stage('npm run deploy:prod') {
		withEnv(["PATH+NPM=${tool 'npm_8_rhel6'}/bin"]) {
		    sh "echo '{\"prod\": {\"username\": \"\$SF_USER\",\"password\": \"\$SF_PW\$SF_TOKEN\",\"loginUrl\": \"\$SF_URL\"}}' > bin/deploy/credentials.json"
		    sh "cat bin/deploy/credentials.json"
			sh "npm run deploy:prod"
		}
	}
}
----------------------------------------------------------------------
node {
	stage('git') {
		git branch: 'devops-test',
			credentialsId: 'CBDC-BITBUCKET-PIPELINE-SSHKEY',
			url: 'ssh://git@code.td.com/hoj/angular-full-app.git'      
	}

    docker.withServer('tcp://rhel-r4vuz.dev.tdct.cloud.td.com:4243') {
        docker.image('jenkinsci/slave-td') {
            sh "uname -a"
        }
    }
}
----------------------------------------------------------------------
for(int i=0; i<3; i++) {
	createJob(i)
}

def createJob(index) {
	pipelineJob("example-${index}") {
		definition {
			cps {
script('''
node ('HOJ-LINUX-AGENT2') {
	stage('build') {
		sh 'wget http://10.87.98.5:8043/angular-full-app.tar.gz'
		sh 'tar -zxvf angular-full-app.tar.gz'
		withEnv(["PATH+NPM=${tool 'npm_8_rhel6'}/bin"]) {
			dir ('angular-full-app') {
    			sh 'npm install'
				withSonarQubeEnv('CBAW-SonarQube') {
					withEnv(["PATH+SONAR=${tool 'sonar-scanner_3.0.3_rhel6'}/bin"]) {
						sh "sonar-scanner"
					}
				}
    		    sh "echo '{\\"prod\\": {\\"username\\": \\"\\$SF_USER\\",\\"password\\": \\"\\$SF_PW\\$SF_TOKEN\\",\\"loginUrl\\": \\"\\$SF_URL\\"}}' > bin/deploy/credentials.json"
    		    sh "cat bin/deploy/credentials.json"
    			sh 'npm run deploy:prod'
			}
		}
	}
}				
''')
				sandbox()
			}
		}
	}
}
----------------------------------------------------------------------
#https://stackoverflow.com/questions/2925606/how-to-create-a-cpu-spike-with-a-bash-command
node ('HOJ-LINUX-AGENT2') {
	// stage('git') {
		// git branch: 'devops-test',
			// credentialsId: 'CBDC-BITBUCKET-PIPELINE-SSHKEY',
			// url: 'ssh://git@code.td.com/hoj/angular-full-app.git'      
	// }
	stage('build') {
		// withMaven(
			// maven: 'maven_3_rhel6',
			// globalMavenSettingsConfig: 'nexus-maven-settings',
			// mavenLocalRepo: '.repository') {
			// sh "mvn --version"
			// sh "mvn archetype:generate -DgroupId=com.mycompany.app -DartifactId=my-app -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false"
			// sh "ls -al my-app"
			// sh "mvn -f my-app/pom.xml clean package"
		// }
		sh 'echo "yes>/dev/null" > yes.sh'
		sh 'echo "timeout 3 ./yes.sh" > timeout.sh'
		sh 'echo "exit 0" >> timeout.sh'
		sh 'echo "echo done" >> timeout.sh'
		sh 'chmod u+x yes.sh timeout.sh'
		for(int i=0; i<15; i++) {
		    sh "./timeout.sh"
		}
	}
}
----------------------------------------------------------------------
node ('HOJ-LINUX-AGENT') {
	stage('git') {
	    sshagent(credentials: ['CBDC-BITBUCKET-PIPELINE-SSHKEY']) {
			for(int i=0; i<10; i++) {
				sh "rm -rf .git"
				sh "git init --quiet"
				if(i%3 == 0) {
					sh "{ time git fetch -q --tags --progress ssh://git@code.td.com/hoj/angular-full-app.git +refs/heads/*:refs/remotes/origin/* ; } 2> time.txt"
					sh "cat time.txt"
					sh "grep real time.txt >> /tmp/tags.txt"
				}
				else if(i%3 == 1) {
					sh "{ time git fetch -q --no-tags --progress ssh://git@code.td.com/hoj/angular-full-app.git +refs/heads/*:refs/remotes/origin/* ; } 2> time.txt"
					sh "cat time.txt"
					sh "grep real time.txt >> /tmp/no-tags.txt"
				}
				else {
					sh "{ time git fetch -q --no-tags --depth=1 --progress ssh://git@code.td.com/hoj/angular-full-app.git +refs/heads/*:refs/remotes/origin/* ; } 2> time.txt"
					sh "cat time.txt"
					sh "grep real time.txt >> /tmp/no-tags-depth-1.txt"
				}
				sh "ls -al"
				sh "sleep 10"
			}
	    }
	}
}
----------------------------------------------------------------------
for(int i=0; i<5; i++)
{
	node ('HOJ-LINUX-AGENT2') {
		stage('sleep') {
			sh 'sleep 5'
		}
	}
}
----------------------------------------------------------------------
def labels = ['HOJ-LINUX-AGENT2', 'HOJ-LINUX-AGENT2'] // labels for Jenkins node types we will build on
def builders = [:]
for (x in labels) {
    def label = x // Need to bind the label variable before the closure - can't do 'for (label in labels)'
    // Create a map to pass in to the 'parallel' step so we can fire all the builds at once
    builders[label] = {
      node(label) {
        withMaven(
            maven: 'maven_3_rhel6',
            globalMavenSettingsConfig: 'nexus-maven-settings') {
            sh "mvn archetype:generate -DgroupId=com.mycompany.app -DartifactId=my-app -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false"
        }
      }
    }
}

parallel builder
----------------------------------------------------------------------
node ('HOJ-LINUX-AGENT2') {
	stage('git') {
		git branch: 'devops-test',
			credentialsId: 'CBDC-BITBUCKET-PIPELINE-SSHKEY',
			url: 'ssh://git@code.td.com/cfp/com.td.cfp.api.git '      
        withMaven(
            maven: 'maven_3_rhel6',
            globalMavenSettingsConfig: 'nexus-maven-settings') {
			input 'Proceed or Abort?'
            sh "mvn clean install"
        }
	}
}
----------------------------------------------------------------------
node ('HOJ-LINUX-AGENT2') {
	stage('git') {
		git branch: 'devops-test',
			credentialsId: 'CBDC-BITBUCKET-PIPELINE-SSHKEY',
			url: 'ssh://git@code.td.com/hoj/angular-full-app.git'      
	    sshagent(credentials: ['CBDC-BITBUCKET-PIPELINE-SSHKEY']) {
    	    sh "git fetch"
	    }
	}
}
----------------------------------------------------------------------
node ('HOJ-LINUX-AGENT2') {
	stage('git') {
		// withCredentials([usernamePassword(credentialsId: 'CBDC-BITBUCKET-PIPELINE-CREDENTIAL', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
			// sh "echo '$USERNAME $PASSWORD' > /tmp/toDelete.txt"
		// }
		// withCredentials([usernameColonPassword(credentialsId: 'CBDC-BITBUCKET-PIPELINE-CREDENTIAL', variable: 'USERPASS')]) {
		withCredentials([usernamePassword(credentialsId: 'CBDC-BITBUCKET-PIPELINE-CREDENTIAL', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
			def repo = "https://PNEX011DEVOPS@code.td.com/scm/rcp/rcp-salesforce.git".split('@')[1]
			sh "git clone --branch develop --depth 1 https://$USERNAME:%23gtp@$repo ."
		}
	}
}
----------------------------------------------------------------------
#!groovy
if(env.BRANCH_NAME.startsWith("fenix-testing")) {
  // repo@fenix-testing/feature --> fenix@fenix-testing/feature
  // repo@fenix-testing/featureDDD (where DDD=digits) --> fenix@fenix-testing/feature
  library("fenix@" + env.BRANCH_NAME.replaceAll("\\d*\$", ""))
}
else {
  library("fenix@master")
}
mainMethod("")
----------------------------------------------------------------------
node ('HOJ-LINUX-AGENT2') {
	stage('tool install') {
		withEnv(["PATH+WHATEVER=${tool 'sfdx-6.50.0'}/bin"]) {
			sh "sfdx --version"
		}
	}
}
----------------------------------------------------------------------
node ('HOJ-LINUX-AGENT') {
	stage('git') {
		withCredentials([usernamePassword(credentialsId: 'CBDC-BITBUCKET-PIPELINE-CREDENTIAL', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
			sh "./echoUP.sh"
		}
	}
}
----------------------------------------------------------------------
import groovy.json.*

try {
    node("HOJ-LINUX-AGENT2") {
        currentBuild.result = "SUCCESS"
        def scm_list = [$class: "GitSCM", branches: [[name: env.GIT_BRANCH]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: env.GIT_CLONE_CREDS, url: env.GIT_CLONE_URL]]]
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
        stage("Clone") {
           checkout([$class: "GitSCM", branches: [[name: env.GIT_BRANCH]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CloneOption', timeout: 30]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: env.GIT_CLONE_CREDS, url: env.GIT_CLONE_URL]]])
        }
        stage ("Checkout local branch") {
            sh """
                git checkout -b integrate/${env.BUILD_TAG}
            """
        }
        stage ("Fetch and Integrate Latest Artifact") {
            withMaven(maven: "maven_3_rhel6", globalMavenSettingsConfig: "nexus-maven-settings")
                def groupID="com.td.hoj"
                def artifactID="td-hoj"
                def version=params.ANGULAR_VERSION
                def packaging="zip"
                sh """
                    if [ -d target ] ; then
                        rm -rv target
                    fi
                    mkdir target
                    mvn dependency:copy -Dartifact=${groupID}:${artifactID}:${version}:${packaging} -DoutputDirectory=${workspace}/target -B
                    cd target
                    unzip td-hoj-*.zip
                    cp -r $WORKSPACE/target/bin/deploy/gulp/.tmp/pages $WORKSPACE/src
                    cp -r $WORKSPACE/target/bin/deploy/gulp/.tmp/staticresources $WORKSPACE/src
                    cd ..
                    rm -rf target
                """
            }
        }
        stage ("Commit Changes") {
            sh """
                git add -A
                git commit -am "HOJ-9672 - Integrating td-hoj into rcp-salesforce" || true
            """
        }
        // stage ("Push to Salesforce") {
        //     sh """
        //         if [ -d "rcp-salesforce" ]; then
        //             rm -rf rcp-salesforce
        //         fi
        //         git clone ssh://git@code.td.com/rcp/rcp-salesforce.git
        //         rm -rf rcp-salesforce/src/pages
        //         rm -rf rcp-salesforce/src/staticresources
        //         cp -r $WORKSPACE/src/pages rcp-salesforce/src
        //         cp -r $WORKSPACE/src/staticresources rcp-salesforce/src
        //         cd rcp-salesforce
        //         git add -A
        //         git commit -am "angular-handoff"
        //         git pull
        //         git push origin $env.GIT_BRANCH
        //         cd ..
        //         rm -rf rcp-salesforce
        //     """
        // }
    }
  catch (e) {
    currentBuild.result="FAILURE"
    println e
} finally {
        
}
----------------------------------------------------------------------
#!groovy
if(env.BRANCH_NAME.startsWith("fenix-testing")) {
  library("fenix@" + env.BRANCH_NAME)
}
else if(env.BRANCH_NAME.equals("load-test/1")) {
  library("fenix@angular")
}
else {
  library("fenix@master")
}
mainMethod("")
----------------------------------------------------------------------
#!groovy
import com.td.jenkins.*
println env.BRANCH_NAME
----------------------------------------------------------------------
if(env.BRANCH_NAME.equals("load-test/1")) {
  @Library("fenix@angular") _
  mainMethod('')
}
else {
  @Library("fenix@master") _
  mainMethod('')
}

    node {
      configFileProvider([configFile(fileId: 'Jenkinsfile-master', variable: 'Jenkinsfile')]) {
        echo "${Jenkinsfile}" 
        sh "cat ${Jenkinsfile}"
        load "$Jenkinsfile"
      }
	}


----------------------------------------------------------------------
java.lang.NoSuchMethodError: No such DSL method 'options' found among steps [ansiColor, archive, bat, build, catchError, checkout, deleteDir, dir, dockerFingerprintFrom, dockerFingerprintRun, dockerNode, echo, emailext, emailextrecipients, envVarsForTool, error, fileExists, findFiles, getContext, git, httpRequest, input, isUnix, jiraComment, jiraIssueSelector, jiraSearch, junit, library, libraryResource, load, lock, mail, milestone, nexusPolicyEvaluation, nexusPublisher, node, nodesByLabel, parallel, powershell, properties, publishHTML, pwd, readFile, readJSON, readManifest, readMavenPom, readProperties, readTrusted, readYaml, resolveScm, retry, script, sh, sha1, sleep, sshagent, stage, stash, step, tee, timeout, tm, tool, touch, unarchive, unstash, unzip, validateDeclarativePipeline, waitForQualityGate, waitUntil, withContext, withCredentials, withDockerContainer, withDockerRegistry, withDockerServer, withEnv, withMaven, wrap, writeFile, writeJSON, writeMavenPom, writeYaml, ws, zip] or symbols [all, allOf, always, ant, antFromApache, antOutcome, antTarget, any, anyOf, apiToken, architecture, archiveArtifacts, artifactManager, artifactsPublisher, attach, authorizationMatrix, batchFile, bitbucket, booleanParam, branch, brokenBuildSuspects, brokenTestsSuspects, buildButton, buildDiscarder, buildingTag, caseInsensitive, caseSensitive, certificate, changeRequest, changelog, changeset, checkoutToSubdirectory, choice, choiceParam, clock, cloud, command, concordionPublisher, configFile, configFileProvider, confluenceAfterToken, confluenceAppendPage, confluenceBeforeToken, confluenceBetweenTokens, confluenceFile, confluencePrependPage, confluenceText, confluenceWritePage, credentials, cron, crumb, culprits, defaultView, demand, dependenciesFingerprintPublisher, developers, disableConcurrentBuilds, disableResume, docker, dockerCert, dockerfile, downloadSettings, downstream, dumb, durabilityHint, envVars, environment, equals, expression, file, fileParam, filePath, findbugsPublisher, fingerprint, frameOptions, freeStyle, freeStyleJob, fromScm, fromSource, git, github, githubPush, globalConfigFiles, headRegexFilter, headWildcardFilter, hyperlink, hyperlinkToModels, inheriting, inheritingGlobal, installSource, invokerPublisher, isRestartedRun, jacoco, jacocoPublisher, jdk, jdkInstaller, jgit, jgitapache, jgivenPublisher, jnlp, jobDsl, jobName, junitPublisher, label, lastDuration, lastFailure, lastGrantedAuthorities, lastStable, lastSuccess, legacy, legacySCM, list, local, location, logRotator, loggedInUsersCanDoAnything, masterBuild, maven, maven3Mojos, mavenErrors, mavenLinkerPublisher, mavenMojos, mavenWarnings, modernSCM, myView, newContainerPerStage, node, nodeProperties, nodejs, nodejsci, nonInheriting, nonStoredPasswordParam, none, not, openTasks, openTasksPublisher, overrideIndexTriggers, paneStatus, parallelsAlwaysFailFast, parameters, password, pattern, permanent, pipeline-model, pipelineGraphPublisher, pipelineMaven, pipelineTriggers, plainText, plugin, pollSCM, preserveStashes, projectNamingStrategy, proxy, publishConfluence, queueItemAuthenticator, quietPeriod, rateLimitBuilds, recipients, remotingCLI, requestor, run, runParam, schedule, scmRetryCount, scriptApprovalLink, search, security, shell, skipDefaultCheckout, skipStagesAfterUnstable, slave, snapshotDependencies, sourceRegexFilter, sourceWildcardFilter, spotbugsPublisher, ssh, sshUserPrivateKey, stackTrace, standard, status, string, stringParam, swapSpace, tag, text, textParam, tmpSpace, toolLocation, triggeredBy, unsecured, upstream, upstreamDevelopers, usernameColonPassword, usernamePassword, veracode, viewsTabBar, weather, withAnt, withSonarQubeEnv, zfs, zip] or globals [createDeployment, currentBuild, deploy, docker, env, mainMethod, maven, notification, npm, params, pipeline, publishConfluence, salesforceDeploy, scan, scm, stormDeploy, test, uploadBlueprint, utility]