#!/bin/bash

delete_vm()
{
    gcloud compute instances delete --zone $zone --quiet $1-$2
}

if [ $# -lt 2 ]; then
  echo 1>&2 "$0: epic_name project_name"
  exit 2
fi

zone=`gcloud --format="value(zone)" compute instances list --regexp=$1-$2`

if [ "$2" == "apache" ]; then
  # ./gcloud-scripts/tear-down-apache.sh $epic_name-apache
  echo $1-apache
else
  # delete_vm $1 $2
  echo $1-$2
fi