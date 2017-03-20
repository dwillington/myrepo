#!/bin/bash

create_vm()
{

    echo $zone
    # gcloud compute instances create $1-$2 \
    # --zone=$zone \
    # --tags $2-ca \
    # --image-family centos-7 \
    # --image-project centos-cloud \
    # --boot-disk-size 50 \
    # --custom-cpu=$3 \
    # --custom-memory=$4 \
    # --metadata-from-file startup-script=./gcloud-scripts/startup.sh
}

if [ $# -lt 1 ]; then
  echo 1>&2 "$0: epic_name"
  exit 2
fi

arr=(us-east1-a us-east1-b us-east1-c)
zone=${arr[$(( ( RANDOM % 3 ) ))]}

create_vm $1 mysql 2 8
create_vm $1 hybris 4 8
create_vm $1 solr 4 8
create_vm $1 aem 2 8
create_vm $1 apache 2 8
