#!/bin/bash

create_vm()
{
    gcloud compute instances create $1-$2 \
    --zone=$zone \
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

# randomly choose a zone for geographically distributed vm's to support failover
# gcloud compute zones list
# arr=(us-east1-b us-east1-c us-east1-d)
# zone=${arr[$(( ( RANDOM % 3 ) ))]}
zone=us-east1-c

create_vm $1 mysql 2 8
create_vm $1 hybris 4 8
create_vm $1 solr 4 8
create_vm $1 aem 2 8

export epic_name=$1
apache_ip=`gcloud --format="value(networkInterfaces[0].accessConfigs[0].natIP)" compute instances list --regexp=$epic_name-apache`
# if apache_ip is empty, create_vm
if [[ -z $apache_ip ]]; then
  create_vm $1 apache 2 8
fi