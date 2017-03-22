yum install unzip -y

cd /opt
mkdir hybris
mkdir jdk

cd hybris
mkdir build

cd /opt/jdk

tar xvf /root/jdk1.8.0_111.tar

update-alternatives --install "/usr/bin/java" "java" "/opt/jdk/jdk1.8.0_111/bin/java" 1
update-alternatives --install "/usr/bin/javac" "javac" "/opt/jdk/jdk1.8.0_111/bin/javac" 1
update-alternatives --install "/usr/bin/javaws" "javaws" "/opt/jdk/jdk1.8.0_111/bin/javaws" 1

cd /opt/hybris

mkdir hybris

cd hybris

unzip -q /root/hybris_6_2_0_0.zip

rm -rf /opt/hybris/hybris/bin/platform 
rm -rf /opt/hybris/hybris/bin/custom 

/usr/bin/unzip -oq /root/hybrisServer-Platform.zip -d /opt/hybris/
/usr/bin/unzip -oq /root/hybrisServer-AllExtensions.zip hybris/bin/custom/* -d /opt/hybris/
/usr/bin/unzip -oq /root/hybrisServer-Config.zip -d /opt/hybris/

#### fix local.properties ####
/root/configure_local_properties.sh
#### fix local.properties ####

/bin/cp -rf /root/mysql-connector-java-5.1.40-bin.jar /opt/hybris/hybris/bin/platform/lib/dbdriver/.

cd /opt/hybris/hybris/bin/platform
chmod 755 *.sh
. ./setantenv.sh
printf '\n' | ant clean all

cd /opt/hybris/hybris/bin/platform/
. ./setantenv.sh
ant initialize | tee /root/initialize.txt

cd /opt/hybris/hybris/bin/platform/tomcat/conf
rm -rf wrapper.conf
cp -p /root/wrapper.conf.base wrapper.conf

cd /opt/hybris/hybris/bin/platform
chmod 755 *.sh
. ./setantenv.sh
ant updatesystem -DconfigFile=/root/config.txt | tee /root/updatesystem.txt

cd /opt/hybris/hybris/bin/platform/tomcat/conf
rm -rf wrapper.conf
cp -p /root/wrapper.conf.base wrapper.conf

export my_public_ip_address=`curl ipinfo.io/ip`
printf '\n' | /opt/jdk/jdk1.8.0_111/bin/keytool -genkey \
    -dname "CN=$my_public_ip_address, OU=I, O=I, L=T, ST=On, C=CA" \
    -alias cloudhybris \
    -validity 3650 -keyalg RSA -keystore /opt/hybris/hybris/bin/platform/tomcat/lib/keystore -storepass 123456 

sh /root/start_hybris.sh
tail -100 `/bin/ls -1td /opt/hybris/hybris/log/tomcat/*| /usr/bin/head -n1`

#timeout 420 sed '/Server startup/q' <(tail -n 0 -f /opt/hybris/hybris/log/tomcat/console-*.log)

# export csrf_token=`curl -b cookie.txt -c cookie.txt -H "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,/;q=0.8" -H "Content-Type: application/x-www-form-urlencoded" -X POST "http://localhost:9001/hac/j_spring_security_check" --data "j_username=admin&j_password=nimda" | grep X-CSRF | cut -d '"' -f12` 
# curl -v -b cookie.txt -c cookie.txt -H "Accept: application/json" -H "Content-Type: application/json; charset=UTF-8" -H "X-CSRF-TOKEN: ${csrf_token}" -X POST "http://localhost:9001/hac/platform/init/execute" --data-binary '{"dropTables":false,"clearHMC":false,"createEssentialData":false,"localizeTypes":false,"allParameters":{"homedepotcainitialdata_sample":"true","lucenesearch_rebuild.indexes":["true"],"lucenesearch_update.index.configuration":["true"],"basecommerce_create geocoding cron job":["no"],"homedepotrelease_runReleaseImpex":["no"],"homedepotcacore_accessRights":["yes"],"homedepotcacockpits_importCustomReports":["yes"],"homedepotcacommercewebservicestest_ImporthomedepotcaTestData":["yes"],"homedepotcatest_createTestData":["no"],"homedepotcastorefront_updateCmsUserRoles":["yes"],"homedepotcastorefront_updateCmsSite":["yes"],"homedepotcastorefront_importWcmsComponents":["yes"],"homedepotcainitialdata_importCoreData":["no"],"homedepotcainitialdata_importSampleData":["no"],"homedepotcainitialdata_importSampleFeeds":["no"],"homedepotcainitialdata_importSampleProdClassFeed":["no"],"homedepotcainitialdata_importSampleMiniFeeds":["yes"],"homedepotcainitialdata_importSampleCuratedFeeds":["no"],"homedepotcainitialdata_activateSolrCronJobs":["no"],"homedepotcainitialdata_importSampleApplianceFeeds":["no"],"homedepotcainitialdata_importSampleProductCatalogs":["no"]},"patches":{},"createProjectData":false,"initMethod":UPDATE}' 

# /opt/jdk/jdk1.8.0_111/bin/keytool -list \
# -keystore /opt/hybris/hybris/bin/platform/tomcat/lib/keystore -storepass 123456 

# /opt/jdk/jdk1.8.0_111/bin/keytool -delete \
# -alias cloudhybris \
# -keystore /opt/hybris/hybris/bin/platform/tomcat/lib/keystore \
# -storepass 123456


