package com.tuc.jenkins.build

public abstract class Builder extends com.tuc.jenkins.Step {

	protected abstract void build(Map config)
	
	protected void execute(Map config) throws Exception {
		build(config)
	}
}