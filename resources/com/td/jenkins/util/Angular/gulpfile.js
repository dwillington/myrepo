'use strict';
const dirtyJson = require('dirty-json');
const gulp = require('gulp');

const env = require('./bin/script/environment.js').get(undefined, '--pod=', 'dev-');
if (!env) { 
  throw new Error('Environment not found');
}

const credentials = require('./bin/deploy/credentials.json')[env.name];
if (!credentials) {
  throw new Error(`Credentials not found for ${env.name}`);
}

env.jsForce.deploy.username = credentials.username;
env.jsForce.deploy.password = credentials.password;
env.jsForce.deploy.resourcePath = env.deployUrl;
env.jsForce.extensions = env.jsForceExt;

require('./bin/deploy/gulp/html')(gulp, env.jsForce);
require('./bin/deploy/gulp/deploy')(gulp, env.jsForce);
