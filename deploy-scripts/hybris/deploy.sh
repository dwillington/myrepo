export SSH_ARGS='-o StrictHostKeyChecking=no'

if [ $# -lt 1 ]; then
  echo 1>&2 "$0: missing epic name"
  exit 2
fi

gsutil rsync -d -r gs://np-cadotcom.appspot.com/ci-builds/epic-builds/ /tmp/epic-builds
export ARTIFACT_LOCATION=/tmp/epic-builds/$1/hybris
export DESTINATION_HOST=$1-hybris

scp $SSH_ARGS $ARTIFACT_LOCATION/*.zip root@$DESTINATION_HOST:/root/.
scp $SSH_ARGS deploy-local.sh root@$DESTINATION_HOST:/root/.

ssh $SSH_ARGS root@$DESTINATION_HOST /root/deploy-local.sh