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
  echo 1>&2 "$0: missing epic_name"
  exit 2
fi

export epic_name=$1
echo "epic_name is set to '$epic_name'";

create_vm $epic_name mysql 1 2
create_vm $epic_name hybris 2 8
create_vm $epic_name solr 1 2
create_vm $epic_name aem 1 4
create_vm $epic_name apache 1 2
