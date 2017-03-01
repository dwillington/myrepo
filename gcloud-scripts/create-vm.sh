#!/bin/bash

create_vm()
{
    gcloud compute instances create $1-$2 \
    --zone=us-east1-c \
    --tags $2-ca \
    --image-family centos-7 \
    --image-project centos-cloud \
    --boot-disk-size 50 \
    --custom-cpu=$3 \
    --custom-memory=$4 \
    --metadata-from-file startup-script=./gcloud-scripts/startup.sh
}

if [ $# -lt 4 ]; then
  echo 1>&2 "$0: epic_name project_name cpu memory"
  exit 2
fi

create_vm $1 $2 $3 $4