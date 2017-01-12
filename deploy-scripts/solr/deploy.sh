export SSH_ARGS='-o StrictHostKeyChecking=no'

if [ $# -lt 1 ]; then
  echo 1>&2 "$0: missing epic name"
  exit 2
fi

export ARTIFACT_LOCATION=/mnt/gcs-bucket/ci-builds/epic-builds/$1/solr
export DESTINATION_HOST=$1-solr

scp $SSH_ARGS $ARTIFACT_LOCATION/homedepot-solr-0.0.1-SNAPSHOT.tar.gz root@$DESTINATION_HOST:/root/.
scp $SSH_ARGS deploy-local.sh root@$DESTINATION_HOST:/root/.

ssh $SSH_ARGS root@$DESTINATION_HOST /root/deploy-local.sh