#!/bin/bash

create_bucket()
{
    gcloud compute instances create $1-$2 \
    --zone=us-west1-b \
    --tags $2-ca \
    --image-family centos-7 \
    --image-project centos-cloud \
    --boot-disk-size 50 \
    --metadata-from-file startup-script=./gcloud-scripts/startup.sh
}

if [ $# -lt 1 ]; then
  echo 1>&2 "$0: missing epic_name"
  exit 2
fi

export epic_name=$1
echo "epic_name is set to '$epic_name'";

create_bucket $epic_name mysql
create_bucket $epic_name hybris
create_bucket $epic_name solr
create_bucket $epic_name aem
create_bucket $epic_name apache
