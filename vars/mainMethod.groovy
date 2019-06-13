import groovy.json.*

def call(body) {
		// evaluate the body block, and collect configuration into the object
		def pipelineParams= [:]
		body.resolveStrategy = Closure.DELEGATE_FIRST
		body.delegate = pipelineParams
		body()

		Map config = null // stores entire job configuration

		// String scmUrl = params.REPO_URL
		// String scmName = scmUrl.split("/")[scmUrl.split("/").size()-1].replace(".git", "")		
		// echo scmName

		echo scmUrl
		echo branch


}