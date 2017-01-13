#!/bin/bash

provision_vm()
{
  cd /bamboo/data/thdutil/serversetup/myrepo/provision-scripts/$2
  ./provision.sh $1-$2
}

if [ $# -lt 1 ]; then
  echo 1>&2 "$0: missing epic_name"
  exit 2
fi

export epic_name=$1
echo "epic-name is set to '$epic_name'";

#    create_vm $epic_name mysql
#    create_vm $epic_name hybris
#    create_vm $epic_name solr
#    create_vm $epic_name aem
#    create_vm $epic_name apache
fi
