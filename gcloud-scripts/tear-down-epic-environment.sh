#! /bin/bash

delete_vm()
{
    gcloud compute instances delete --quiet $1-$2
}

if [ $# -lt 1 ]; then
  echo 1>&2 "$0: missing epic_name"
  exit 2
fi

export epic_name=$1
echo "epic_name is set to '$epic_name'";

delete_vm $epic_name solr
delete_vm $epic_name mysql
delete_vm $epic_name hybris
delete_vm $epic_name apache
delete_vm $epic_name aem





