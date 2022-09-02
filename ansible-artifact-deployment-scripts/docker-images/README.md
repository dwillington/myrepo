This image allows you to create a clean docker container to test your ansible scripts against, from scratch.

log on to a server with docker and verify you can perform "docker ps -a"

```
ssh aashraf@blxsanddev1                 # JBoss DEV 1
docker ps -a
```

paste the contents of centos7-ansible/Dockerfile as follows

```
mkdir -p docker-images/centos7-ansible/
cd ~/docker-images/centos7-ansible/
echo "" > Dockerfile
vi Dockerfile
```

you will want to change aashraf to your username in Dockerfile

make sure /tmp/jboss-eap-6.4.tgz is available, which is just a tgz of /opt/jboss/jboss-eap-6.4, without the modules folder

```
ls -al /tmp/jboss-eap-6.4.tgz
# if /tmp/jboss-eap-6.4.tgz does not exist
cd /tmp
wget http://blxblddev3:8081/artifactory/generic-local/jboss-eap-6.4.tgz
cp /tmp/jboss-eap-6.4.tgz ~/docker-images/centos7-ansible/.
```

below will create the centos7-ansible image
```
cd ~/docker-images/centos7-ansible/
docker stop $(docker ps -a | grep centos7-ansible | awk '{ print $1 }')
docker rm $(docker ps -a -f status=exited -q) && docker ps -a
docker rmi centos7-ansible
docker build -f ~/docker-images/centos7-ansible/Dockerfile . -t centos7-ansible
```

stop and remove any previously running "--name=jboss6" containers
```
docker stop $(docker ps -a | grep jboss6 | awk '{ print $1 }')
docker rm $(docker ps -a -f status=exited -q) && docker ps -a
```

now start the container, bash into it, and set your password

note, the port mappings "-p 8092:8090 -p 8082:8080 -p 9082:9080" correspond to jboss, spring boot, and tomcat

```
port=2022
docker container run -it --privileged -e "container=docker" \
  -v /sys/fs/cgroup:/sys/fs/cgroup \
  -v /opt/jboss/jboss-eap-6.4/modules:/opt/jboss/jboss-eap-6.4/modules:ro \
  -p 8092:8090 -p 8082:8080 -p 9082:9080 -p $port:22 \
  -d --name=jboss6 centos7-ansible 
docker exec -it $(docker ps -a | grep jboss6 | awk '{ print $1 }') bash
rm -rf /run/nologin
passwd aashraf
```

from ansible tower, blxansdev1, make sure you can ssh into this container

first make sure you have generated an ssh key pair

```
ssh aashraf@blxansdev1                  # Ansible Tower DEV
ls -al ~/.ssh
# perform ssh-keygen if you don't have keys
```

```
ssh aashraf@blxansdev1                  # Ansible Tower DEV
port=2022
ssh-copy-id -o "UserKnownHostsFile=/dev/null" -o "StrictHostKeyChecking=no" -p $port aashraf@blxsanddev1
ssh -o "UserKnownHostsFile=/dev/null" -o "StrictHostKeyChecking=no" -p $port aashraf@blxsanddev1
exit
```

now you can run a playbook against this container from blxansdev1

```
export ANSIBLE_HOST_KEY_CHECKING=False
export ANSIBLE_SSH_ARGS="-o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no"
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
```






```
```

when running as "--net=host", you can also change the ssh port 2022 in Dockerfile to avoid conflicts with other users performing tests

```
RUN sed -i 's/#Port 22/Port 2022/' /etc/ssh/sshd_config
```

