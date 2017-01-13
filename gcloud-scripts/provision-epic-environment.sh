#!/bin/bash

provision_vm()
{
  cd provision-scripts/$2
  ./provision.sh $1-$2
}

if [ $# -lt 1 ]; then
  echo 1>&2 "$0: missing epic_name"
  exit 2
fi

export epic_name=$1
echo "epic_name is set to '$epic_name'";

provision_vm $epic_name mysql
provision_vm $epic_name hybris
provision_vm $epic_name solr
provision_vm $epic_name aem
provision_vm $epic_name apache
