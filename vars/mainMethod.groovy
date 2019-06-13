import groovy.json.*

def call(body) {
		Map config = null // stores entire job configuration

		String scmUrl = params.REPO_URL
		echo scmUrl

		String scmName = scmUrl.split("/")[scmUrl.split("/").size()-1].replace(".git", "")		
		echo scmName

}