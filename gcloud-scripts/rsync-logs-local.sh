#!/bin/bash

rsync_logs()
{
  mkdir -p $4
  rsync -chavzeP "ssh -o StrictHostKeyChecking=no" --delete --stats root@$1-$2:$3 $4
}

if [ $# -lt 1 ]; then
  echo 1>&2 "$0: missing epic_name"
  exit 2
fi

rsync_logs $1 solr /opt/solr/homedepot-solr/server/logs /root/log-server/$1-solr
rsync_logs $1 aem /opt/adobe/publish/crx-quickstart/logs/ /root/log-server/$1-aem
rsync_logs $1 hybris /opt/hybris/hybris/log/tomcat/ /root/log-server/$1-hybris
