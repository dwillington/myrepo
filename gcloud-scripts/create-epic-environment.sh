#!/bin/bash

create_vm()
{
    gcloud compute instances create $1-$2 \
    --zone=us-west1-b \
    --tags $2-ca \
    --image-family centos-7 \
    --image-project centos-cloud \
    --boot-disk-size 50 \
    --custom-cpu=1 \
    --custom-memory=$3 \
    --metadata-from-file startup-script=./gcloud-scripts/startup.sh
}

if [ $# -lt 1 ]; then
  echo 1>&2 "$0: missing epic_name"
  exit 2
fi

export epic_name=$1
echo "epic_name is set to '$epic_name'";

create_vm $epic_name mysql 2
create_vm $epic_name hybris 2
create_vm $epic_name solr 2
create_vm $epic_name aem 3.75
create_vm $epic_name apache 2
