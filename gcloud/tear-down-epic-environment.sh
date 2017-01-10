#! /bin/bash

delete_vm()
{
    gcloud compute instances delete --quiet $1-$2
}

if [ -z ${epic_name+x} ]; then 
    echo "epic_name is unset";
else
    echo "epic-name is set to '$epic_name'";
    delete_vm $epic_name solr
    delete_vm $epic_name mysql
    delete_vm $epic_name hybris
    delete_vm $epic_name apache
    delete_vm $epic_name aem
fi





