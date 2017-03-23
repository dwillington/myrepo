#!/bin/bash

if [ $# -lt 1 ]; then
  echo 1>&2 "$0: missing epic_name"
  exit 2
fi

SSH_ARGS='-o StrictHostKeyChecking=no'
scp $SSH_ARGS gcloud-scripts/rsync-logs-local.sh root@log-server:/root/rsync-logs-local.sh
ssh $SSH_ARGS root@log-server "/root/rsync-logs-local.sh $1"