#!/bin/bash

provision_vm()
{
  cd provision-scripts/$2
  ./provision.sh $1-$2
  cd ../../
}

if [ $# -lt 2 ]; then
  echo 1>&2 "$0: missing epic_name project_name"
  exit 2
fi

provision_vm $1 $2