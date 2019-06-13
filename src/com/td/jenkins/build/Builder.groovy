package com.td.jenkins.build

public abstract class Builder extends com.td.jenkins.Step {

	protected abstract void build(Map config)
	
	protected void execute(Map config) throws Exception {
		build(config)
	}
}