#!/bin/sh

cid=$(sudo docker ps -a | grep 9080 | sed 's/|/ /' | awk '{print $1}')
docker cp AssetMgr/target/AssetMgr.war $cid:/tmp/.
docker cp deploy-scripts/AssetMgr/bin/deploy.sh $cid:/tmp/.
docker exec $cid /tmp/deploy.sh