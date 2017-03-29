#!/bin/bash

if [ $# -lt 1 ]; then
  echo 1>&2 "$0: missing epic_name"
  exit 2
fi

export epic_name=$1
#echo "epic_name is set to '$epic_name'";

export random_folder=delete-me-$RANDOM
mkdir -p /tmp/$random_folder

if [[ $(hostname) = ln0a7b.* ]]; then
  PATH=/bamboo/data/thdutil/serversetup/google-cloud-sdk/bin:$PATH
elif [[ $(hostname) = ld8139* ]]; then
  PATH=/bamboo/data/thdutil/serversetup/google-cloud-sdk/bin:$PATH
elif [[ $(hostname) = ld5717* ]]; then
  PATH=/root/google-cloud-sdk/bin:$PATH
fi

# https://cloud.google.com/sdk/gcloud/reference/topic/projections
gcloud --format="value(networkInterfaces[0].accessConfigs[0].natIP)" compute instances list --regexp=$epic_name-.* > /tmp/$random_folder/out.txt
sed -i -e 's/^/http:\/\//' /tmp/$random_folder/out.txt
sed -i '1s/$/\/libs\/granite\/core\/content\/login.html/' /tmp/$random_folder/out.txt
sed -i '2s/$/\/en\/home.html/' /tmp/$random_folder/out.txt
sed -i '3s/$/\/hmc\/hybris/' /tmp/$random_folder/out.txt
sed -i '4s/$/\/phpmyadmin\//' /tmp/$random_folder/out.txt
sed -i '5s/$/\/solr\//' /tmp/$random_folder/out.txt

if [ $# -lt 2 ]
then
  cat /tmp/$random_folder/out.txt
else
  export project_name=$2
  case $project_name in
    "aem" )
        sed -n 1p /tmp/$random_folder/out.txt;;
    "apache" )
        sed -n 2p /tmp/$random_folder/out.txt;;
    "hybris" )
        sed -n 3p /tmp/$random_folder/out.txt;;
    "mysql" )
        sed -n 4p /tmp/$random_folder/out.txt;;
    "solr" )
        sed -n 5p /tmp/$random_folder/out.txt;;
  esac
fi

rm -rf /tmp/$random_folder/