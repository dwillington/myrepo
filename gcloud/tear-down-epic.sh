#! /bin/bash

if [ -z ${epic_name+x} ]; then 
    echo "epic_name is unset";
else
    echo "epic-name is set to '$epic_name'";
    delete_vm $epic_name solr
fi





delete_vm()
{
    gcloud compute instances delete --quiet $1-$2 \
}