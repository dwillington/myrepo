#!/bin/bash

rsync_logs()
{
  vm_ip=`gcloud --format="value(networkInterfaces[0].accessConfigs[0].natIP)" compute instances list --regexp=$1-$2`
  if [[ ! -z $vm_ip ]]; then
    mkdir -p $4
    time rsync -chavzPe "ssh -o StrictHostKeyChecking=no" --delete --stats \
         --files-from=<(ssh -o StrictHostKeyChecking=no root@$1-$2 "find $3 -mtime -1 -type f -exec basename {} \;") root@$1-$2:$3 $4
    # http://www.manpagez.com/man/1/rsync/
  fi
}

export SSH_ARGS='-o StrictHostKeyChecking=no'

if [ $# -lt 1 ]; then
  echo 1>&2 "$0: missing epic_name"
  exit 2
fi

rsync_logs $1 solr /opt/solr/homedepot-solr/server/logs/ /root/log-server/$1-solr
rsync_logs $1 aem /opt/adobe/publish/crx-quickstart/logs/ /root/log-server/$1-aem
rsync_logs $1 hybris /opt/hybris/hybris/log/tomcat/ /root/log-server/$1-hybris

ssh $SSH_ARGS root@$1-apache docker cp apache:/opt/apache/logs /tmp/
rsync_logs $1 apache /tmp/logs/ /root/log-server/$1-apache

# https://superuser.com/questions/297342/rsync-files-newer-than-1-week
# To set that as a remote filter: rsync -avn --files-from=<(ssh user@A 'find /path/on/A/ -mtime -7 -type f -exec basename {} \;') user@A:/path/on/A/ user@B:targetdir