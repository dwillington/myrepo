#!/bin/bash

provision_vm()
{
  cd provision-scripts/$2
  ./provision.sh $1-$2
  cd ../../
}

if [ $# -lt 1 ]; then
  echo 1>&2 "$0: epic_name"
  exit 2
fi

provision_vm $1 mysql
provision_vm $1 hybris
provision_vm $1 solr
provision_vm $1 aem
provision_vm $1 apache
