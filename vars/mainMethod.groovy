#!/usr/bin/groovy

def call(body) {
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

	def instance = this.class.classLoader.loadClass( "mavenPipeline", true, false )?.newInstance()
	instance( pipelineParams )

}