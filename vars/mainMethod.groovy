import groovy.json.*

def call(body) {
		Map config = null // stores entire job configuration

		properties([parameters([string(name: 'REPO_URL'])])
		echo params.REPO_URL

		String scmName = params.REPO_URL.split("/")[scmUrl.split("/").size()-1].replace(".git", "")
		
		echo scmName

}