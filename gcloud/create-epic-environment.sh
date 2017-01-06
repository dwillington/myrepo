#!/bin/bash

create_vm()
{
    gcloud compute instances create $1-$2 \
    --zone=us-west1-b \
    --tags $2-ca \
    --image-family centos-7 \
    --image-project centos-cloud \
    --boot-disk-size 50 \
    --metadata-from-file startup-script=./startup.sh
}

if [ -z ${epic_name+x} ]; then 
    echo "epic_name is unset";
else
    echo "epic-name is set to '$epic_name'";
    create_vm $epic_name aem
    create_vm $epic_name apache
    create_vm $epic_name hybris
    create_vm $epic_name mysql
    create_vm $epic_name solr
fi
