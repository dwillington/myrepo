#!/bin/sh

export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.131-3.b12.el7_3.x86_64/jre
cd /tcserver/dev/instance1
/apps/tcserver/tcruntime-instance.sh create AssetMgrSvr
mv /tmp/AssetMgr.war /tcserver/dev/instance1/AssetMgrSvr/webapps/.
/apps/tcserver/tcruntime-ctl.sh AssetMgrSvr stop
/apps/tcserver/tcruntime-ctl.sh AssetMgrSvr start
