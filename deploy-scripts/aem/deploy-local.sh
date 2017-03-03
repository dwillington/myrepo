#/opt/adobe/publish/crx-quickstart/bin/stop

#cd /opt/adobe/publish
#. ./setenv.sh 

#/opt/adobe/publish/crx-quickstart/bin/start

curl -u admin:admin -F file=@"/root/homedepot.ca.homedepot-apps.zip" -F name="homedepot-apps" -F force=true -F install=true http://localhost:4503/crx/packmgr/service.jsp 

tail -100 /opt/adobe/publish/crx-quickstart/logs/stdout.log