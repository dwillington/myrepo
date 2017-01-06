#! /bin/bash

if [ -z ${epic_name+x} ]; then 
    echo "epic_name is unset";
else
    echo "epic-name is set to '$epic_name'";
    create_vm() $epic_name solr
fi





create_vm()
{
    gcloud compute instances create $1-$2 \
    --tags $2-ca ^
    --image-family centos-7 ^
    --image-project centos-cloud ^
    --boot-disk-size 50 ^
    --metadata-from-file startup-script=./startup.sh
}