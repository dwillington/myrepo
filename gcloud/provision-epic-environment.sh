#!/bin/bash

provision_vm()
{
  cd /bamboo/data/thdutil/serversetup/myrepo/provision-scripts/$2
  ./provision.sh $1-$2
}

if [ -z ${epic_name+x} ]; then 
    echo "epic_name is unset";
else
    echo "epic-name is set to '$epic_name'";
    provision_vm $epic_name solr
#    create_vm $epic_name apache
#    create_vm $epic_name hybris
#    create_vm $epic_name mysql
#    create_vm $epic_name solr
fi
