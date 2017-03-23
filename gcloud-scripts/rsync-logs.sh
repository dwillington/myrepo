#!/bin/bash

rsync_logs()
{
  ssh root@log-server "mkdir -p $4"
  ssh root@log-server "rsync -chavzP --delete --stats root@$1-$2:$3 $4"
}

if [ $# -lt 1 ]; then
  echo 1>&2 "$0: missing epic_name"
  exit 2
fi

rsync_logs $1 solr /opt/solr/homedepot-solr/server/logs root@log-server:/root/log-server/$1-solr
rsync_logs $1 aem /opt/adobe/publish/crx-quickstart/logs/ root@log-server:/root/log-server/$1-aem
rsync_logs $1 hybris /opt/hybris/hybris/log/tomcat/ root@log-server:/root/log-server/$1-hybris
