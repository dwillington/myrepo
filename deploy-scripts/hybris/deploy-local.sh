/root/stop_hybris.sh

rm -rf /opt/hybris/hybris/bin/platform 
rm -rf /opt/hybris/hybris/bin/custom 

echo "Unzipping Hybris Platform.zip"
/usr/bin/unzip -o /root/hybrisServer-Platform.zip -d /opt/hybris/

echo "Unzipping All Extension"
/usr/bin/unzip -o /root/hybrisServer-AllExtensions.zip hybris/bin/custom/* -d /opt/hybris/

echo "Unzipping Hybris config"
/usr/bin/unzip -o /root/hybrisServer-Config.zip -d /opt/hybris/

/bin/cp -rf ~/mysql-connector-java-5.1.35-bin.jar /opt/hybris/hybris/bin/platform/lib/dbdriver/.

echo "Changing to platform directory"
cd /opt/hybris/hybris/bin/platform

chmod 755 *.sh
. ./setantenv.sh
printf '\n' | ant clean all

cd /opt/hybris/hybris/config
rm -rf local.properties
cp -p /root/local.properties.base local.properties

cd /opt/hybris/hybris/bin/platform/tomcat/conf
rm -rf wrapper.conf
cp -p /root/wrapper.conf.base wrapper.conf

sh /root/start_hybris.sh

ls -al /opt/hybris/hybris/log/tomcat
#tail -100 /opt/hybris/hybris/log/tomcat/console-*.log
