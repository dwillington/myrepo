#!/bin/sh

export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.131-3.b12.el7_3.x86_64/jre
application_name=AssetMgr
application_server_name=$(printf "%s%s" $application_name 'Srv')
cd /tcserver/dev/instance1

if [ ! -d "/tcserver/dev/instance1/$application_server_name" ]; then
  /apps/tcserver/tcruntime-instance.sh create $application_server_name
fi

mv /tmp/$application_name.war /tcserver/dev/instance1/$application_server_name/webapps/.
/apps/tcserver/tcruntime-ctl.sh $application_server_name stop
/apps/tcserver/tcruntime-ctl.sh $application_server_name start