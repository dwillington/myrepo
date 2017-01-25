export SSH_ARGS='-o StrictHostKeyChecking=no'

if [ $# -lt 1 ]; then
  echo 1>&2 "$0: missing epic name"
  exit 2
fi

gsutil rsync -d -r gs://np-cadotcom.appspot.com/ci-builds/epic-builds/ /tmp/epic-builds
export ARTIFACT_LOCATION=/tmp/epic-builds/$1/apache
export DESTINATION_HOST=$1-apache

/bin/cp -rf apache_deploy_on_docker_orig.sh apache_deploy_on_docker.sh
export epic_name=${DESTINATION_HOST%-*}
sed -i -e "s/aem-hostname/$epic_name-aem/g" apache_deploy_on_docker.sh
echo "setting aem hostname to $epic_name-aem in apache_deploy_on_docker.sh"

scp $SSH_ARGS $ARTIFACT_LOCATION/homedepot-httpd-0.0.1-SNAPSHOT.tar.gz root@$DESTINATION_HOST:/root/.
scp $SSH_ARGS deploy-local.sh root@$DESTINATION_HOST:/root/.
scp $SSH_ARGS apache_deploy_on_docker.sh root@$DESTINATION_HOST:/root/.

ssh $SSH_ARGS root@$DESTINATION_HOST /root/deploy-local.sh