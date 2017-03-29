/root/stop_hybris.sh

rm -rf /opt/hybris/hybris/bin/platform 
rm -rf /opt/hybris/hybris/bin/custom

echo "Unzipping Hybris Platform.zip"
/usr/bin/unzip -o /root/hybrisServer-Platform.zip -d /opt/hybris/

echo "Unzipping All Extension"
/usr/bin/unzip -o /root/hybrisServer-AllExtensions.zip hybris/bin/custom/* -d /opt/hybris/

echo "Unzipping Hybris config"
/usr/bin/unzip -o /root/hybrisServer-Config.zip -d /opt/hybris/

/bin/cp -rf ~/mysql-connector-java-5.1.40-bin.jar /opt/hybris/hybris/bin/platform/lib/dbdriver/.

echo "Changing to platform directory"
cd /opt/hybris/hybris/bin/platform

chmod 755 *.sh
. ./setantenv.sh
printf '\n' | ant clean all

#### fix local.properties ####
/root/configure_local_properties.sh
#### fix local.properties ####

rm -rf /opt/hybris/hybris/bin/platform/tomcat/conf/wrapper.conf
cp -p /root/wrapper.conf.base /opt/hybris/hybris/bin/platform/tomcat/conf/wrapper.conf

export my_public_ip_address=`curl ipinfo.io/ip`
printf '\n' | /opt/jdk/jdk1.8.0_111/bin/keytool -genkey \
    -dname "CN=$my_public_ip_address, OU=I, O=I, L=T, ST=On, C=CA" \
    -alias cloudhybris \
    -validity 3650 -keyalg RSA -keystore /opt/hybris/hybris/bin/platform/tomcat/lib/keystore -storepass 123456 

sh /root/start_hybris.sh

ls -al /opt/hybris/hybris/log/tomcat
tail `/bin/ls -1td /opt/hybris/hybris/log/tomcat/*| /usr/bin/head -n1`


