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

if [ $# -lt 1 ]; then
  echo 1>&2 "$0: epic_name"
  exit 2
fi

create_vm $1 mysql 2 8
create_vm $1 hybris 4 8
create_vm $1 solr 4 8
create_vm $1 aem 2 8
create_vm $1 apache 2 8
