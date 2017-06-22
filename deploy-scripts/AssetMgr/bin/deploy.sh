#!/bin/sh

if [ $# -lt 1 ] 
then
    echo "deploy.sh port"
    exit 1
fi

port=$1
application_name=AssetMgr
cid=$(docker ps -a | grep $port | sed 's/|/ /' | awk '{print $1}')
docker cp $application_name/target/$application_name.war $cid:/tmp/.
docker cp deploy-scripts/$application_name/bin/deploy-local.sh $cid:/tmp/.
docker exec $cid /tmp/deploy-local.sh

echo "deployed to http://$(hostname):$port/$application_name"