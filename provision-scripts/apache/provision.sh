export SSH_ARGS='-o StrictHostKeyChecking=no'
export INSTALL_REPOSITORY=/bamboo/data/thdutil/serversetup/Apache
export DESTINATION_HOST=$1

/bin/cp -rf apache_setup_on_docker_orig.sh apache_setup_on_docker.sh
export epic_name=${DESTINATION_HOST%-*}
sed -i -e "s/epic_name/$epic_name/g" apache_setup_on_docker.sh
echo "setting epic_name hostname to $epic_name-aem in apache_setup_on_docker.sh"

scp $SSH_ARGS apache_setup_base.sh root@$DESTINATION_HOST:/root/.
scp $SSH_ARGS apache_setup_on_docker.sh root@$DESTINATION_HOST:/root/.
ssh $SSH_ARGS root@$DESTINATION_HOST chmod u+x /root/*.sh

scp $SSH_ARGS $INSTALL_REPOSITORY/*.gz root@$DESTINATION_HOST:/root/.

# stage a pre built deployment artifact from cloud storage to DESTINATION_HOST
# gsutil rsync -d -r gs://np-cadotcom.appspot.com/ci-builds/epic-builds/ /tmp/epic-builds
# export epic_name=${DESTINATION_HOST%-*}
# export ARTIFACT_LOCATION=/tmp/epic-builds/$epic_name/apache
# scp $SSH_ARGS $ARTIFACT_LOCATION/*.gz root@$DESTINATION_HOST:/root/.

ssh $SSH_ARGS root@$DESTINATION_HOST /root/apache_setup_base.sh
