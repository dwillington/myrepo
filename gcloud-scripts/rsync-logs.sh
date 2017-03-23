#!/bin/bash

if [ $# -lt 1 ]; then
  echo 1>&2 "$0: missing epic_name"
  exit 2
fi

SSH_ARGS='-o StrictHostKeyChecking=no'
scp $SSH_ARGS gcloud-scripts/rsync-logs-local.sh root@log-server:/root/rsync-logs-local.sh
ssh $SSH_ARGS root@log-server "/root/rsync-logs-local.sh $1"

# ssh $SSH_ARGS root@log-server "mkdir -p /root/log-server/epic1-solr"
# ssh $SSH_ARGS root@log-server "mkdir -p /root/log-server/epic1-solr rsync -chavzPe \"ssh -o StrictHostKeyChecking=no\" --delete --stats root@epic1-solr:/opt/solr/homedepot-solr/server/logs /root/log-server/epic1-solr"


