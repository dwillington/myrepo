#!/bin/bash

delete_vm()
{
    gcloud compute instances delete --zone $zone --quiet $1-$2
}

if [ $# -lt 1 ]; then
  echo 1>&2 "$0: missing epic_name"
  exit 2
fi

epic_name=$1

# parse out the zone
zone=`gcloud --format="value(zone)" compute instances list --regexp=$epic_name-mysql`

delete_vm $epic_name solr
delete_vm $epic_name mysql
delete_vm $epic_name hybris
#delete_vm $epic_name apache
./gcloud-scripts/tear-down-apache.sh $epic_name-apache
delete_vm $epic_name aem





