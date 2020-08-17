## Ansible Playbooks

Ansible Playbooks for artifact deployment are stored below:
http://blxblddev3.transunion.ca:10002/build-support/ansible-artifact-deployment-scripts/files/tip/

The current layout has a role for each type of deployment: jboss, spring-boot, tomcat, etc. And some extra roles for installation of tools such as hg (mercurial), jboss-post-installation for tuc modules, etc.

Any common variables are stored in group_vars:
http://blxblddev3.transunion.ca:10002/build-support/ansible-artifact-deployment-scripts/files/da9bac7f8030275ddac704986c2207574e65f32d/group_vars/all

### Running Ansible Playbook

One can run an Ansible playbook from the command line.
```
ssh aashraf@blxansdev1                  # Ansible Tower DEV
password=password
hg_url=http://admin:$password@blxblddev3.transunion.ca:10002/build-support/ansible-artifact-deployment-scripts
hg clone $hg_url
```

This will clone the ansible-artifact-deployment-scripts in to your home folder.
Make sure you can ssh to the server you want to run the playbook against.

```
ls -al ~/.ssh # have you already generated keys?
ssh-keygen    # if you have not generated keys
# copy your public key to server on which you will run your ansible playbook
ssh-copy-id -o "UserKnownHostsFile=/dev/null" -o "StrictHostKeyChecking=no" aashraf@blxsanddev1
# confirm you can ssh on to server
ssh -o "UserKnownHostsFile=/dev/null" -o "StrictHostKeyChecking=no"  aashraf@blxsanddev1
exit
```

exit back to blxansdev1 and now run the playbook
```
cd ~/ansible-artifact-deployment-scripts
-------------------------------------------------------------------------------
jboss-deployment
-------------------------------------------------------------------------------
port=22
cd ~/ansible-artifact-deployment-scripts/
echo "---
  ansible_become: yes
  ansible_become_method: sudo
  ansible_become_user: jbs1app
  RhodeCode_password: password
  projectName: c2gateway
  repoName: tuc-c2-gateway
  artifactoryRepository: libs-release-local
  rhodecodeUrl: 'blxblddev3.transunion.ca:10002/apps/services/tuc-c2-gateway'
  pomGroupIdAnsible: ca.tuc.c2gateway
  pomAritfactIdAnsible: tuc-c2-gateway
  pomVersionAnsible: 1.0.3
  pomAritfactExtensionAnsible: war
  ansible_ssh_port: $port
" > extra-vars.yaml
ansible-playbook -i blxsanddev1, jboss-deployment.yml \
--ask-become-pass \
--extra-vars @extra-vars.yaml
-------------------------------------------------------------------------------
spring-boot-deployment
-------------------------------------------------------------------------------
cd ~/ansible-artifact-deployment-scripts/
echo "---
  ansible_become: yes
  ansible_become_method: sudo
  ansible_become_user: jbs1app
  RhodeCode_password: password
  projectName: c2gateway-ffr
  repoName: tuc-c2-gateway-ffr-jws
  artifactoryRepository: libs-snapshot-local
  rhodecodeUrl: 'blxblddev3.transunion.ca:10002/apps/services/tuc-c2-gateway-ffr-jws'
  pomGroupIdAnsible: ca.tuc.c2gateway
  pomAritfactIdAnsible: tuc-c2-gateway-ffr-jws
  pomVersionAnsible: 1.0.0-SNAPSHOT
  pomAritfactExtensionAnsible: jar
  serverEnvironment: dev
  serverPort: 8090
" > extra-vars.yaml
ansible-playbook -i blxsanddev2, spring-boot-deployment.yml \
--ask-become-pass \
--extra-vars @extra-vars.yaml
-------------------------------------------------------------------------------
tomcat8-deployment
-------------------------------------------------------------------------------
port=22
cd ~/ansible-artifact-deployment-scripts/
echo "---
  ansible_become: yes
  ansible_become_method: sudo
  ansible_become_user: jbs1app
  RhodeCode_password: password
  projectName: c2gateway-ffr
  repoName: tuc-c2-gateway-ffr-tmct
  artifactoryRepository: libs-snapshot-local
  rhodecodeUrl: 'blxblddev3.transunion.ca:10002/apps/services/tuc-c2-gateway-ffr-tmct'
  pomGroupIdAnsible: ca.tuc.c2gateway
  pomAritfactIdAnsible: tuc-c2-gateway-ffr-tmct
  pomVersionAnsible: 1.0.0-SNAPSHOT
  pomAritfactExtensionAnsible: war
  serverEnvironment: dev
  ansible_ssh_port: $port
" > extra-vars.yaml
ansible-playbook -i blxsanddev2, tomcat8-deployment.yml \
--ask-become-pass \
--extra-vars @extra-vars.yaml
```

### Privilege Escalation in Ansible Tower

![privilege-escalation](docs/images/privilege-escalation.png)

The ansible-artifact-deployment-scripts-jboss playbook runs as ans1app service account user. The playbook needs to run as jbs1app local account user on blxsanddev1. This is achieved with the following variables.

  ansible_become: yes
  ansible_become_method: sudo
  ansible_become_user: jbs1app

This will require Privilege Escalation which will require a Privilege Escalation password.

![privilege-escalation-password](docs/images/privilege-escalation-password.png)




```
```
