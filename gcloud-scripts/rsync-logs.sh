#!/bin/bash

rsync_logs()
{
  mkdir -p $4 && rsync -chavzP --delete --stats root@$1-$2:$3 $4
}

rsync_logs epic1 solr /opt/solr/homedepot-solr/server/logs /root/log-server/epic1-solr
rsync_logs epic1 aem /opt/adobe/publish/crx-quickstart/logs/ /root/log-server/epic1-aem
rsync_logs epic1 hybris /opt/hybris/hybris/log/tomcat/ /root/log-server/epic1-hybris
