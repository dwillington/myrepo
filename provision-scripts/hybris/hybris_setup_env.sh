echo "                "
echo "Extracting Java File"
echo "                "

cd /opt/jdk
tar xvf ~/jdk1.7.0_75.tar

echo "               "
echo "Start - Setting Java Base"
echo "               "

update-alternatives --install "/usr/bin/java" "java" "/opt/jdk/jdk1.7.0_75/bin/java" 1
update-alternatives --install "/usr/bin/javac" "javac" "/opt/jdk/jdk1.7.0_75/bin/javac" 1
update-alternatives --install "/usr/bin/javaws" "javaws" "/opt/jdk/jdk1.7.0_75/bin/javaws" 1

echo "               "
echo "End - Setting Java Base"
echo "               "

cd /opt/hybris

echo " "
echo "Start - Extract file"
echo " "

unzip ~/HYBRISCOMM5400_0.ZIP

echo " "
echo "End - Extract file"
echo " "

cd /opt/hybris/hybris/bin/platform
chmod 755 *.sh
. ./setantenv.sh
printf '\n' | ant clean all

# this below represents deploy, ant clean all
rm -rf /opt/hybris/hybris/bin
/usr/bin/unzip -o /root/build/hybrisServer-Platform.zip -d /opt/hybris/
/usr/bin/unzip -o /root/build/hybrisServer-AllExtensions.zip -d /opt/hybris/
/usr/bin/unzip -o /root/build/hybrisServer-Config.zip -d /opt/hybris/

cd /opt/hybris/hybris/config
rm -rf local.properties
cp -p /root/local.properties.base local.properties
#/bin/cp -rf ~/local.properties /opt/hybris/hybris/config/.
cp ~/mysql-connector-java-5.1.35-bin.jar /opt/hybris/hybris/bin/platform/lib/dbdriver/.

cp -p ~/QA_PLCC-hdca.jck /opt/hybris/security/
cp -p ~/HD.ca-BankToken-QA.pfx /opt/hybris/security/

cd /opt/hybris/hybris/bin/platform
. ./setantenv.sh
ant initialize > out.txt

cd /opt/hybris/hybris/bin/platform/tomcat/conf
rm -rf wrapper.conf
cp -p /root/wrapper.conf.base wrapper.conf

sh ~/start_hybris.sh

tail -100 /opt/hybris/hybris/bin/platform/tomcat/logs/console-*.log

#csrf_token=`curl -b cookie.txt -c cookie.txt -H "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,/;q=0.8" -H "Content-Type: application/x-www-form-urlencoded" -X POST "http://localhost:9001/j_spring_security_check" --data "j_username=admin&j_password=nimda" | grep X-CSRF | cut -d '"' -f12`
#curl -v -b cookie.txt -c cookie.txt -H "Accept: application/json" -H "Content-Type: application/json; charset=UTF-8" -H "X-CSRF-TOKEN: ${csrf_token}" -X POST "http://localhost:9001/hac/platform/init/execute" --data-binary ' 
#{"dropTables":true,"clearHMC":true,"createEssentialData":true,"localizeTypes":true,"allParameters":{"core_sample":"true","scripting_sample":"true","mediaweb_sample":"true","commons_sample":"true","processing_sample":"true","impex_sample":"true","validation_sample":"true","catalog_sample":"true","europe1_sample":"true","platformservices_sample":"true","workflow_sample":"true","comments_sample":"true","advancedsavedquery_sample":"true","embeddedserver_sample":"true","tomcatembeddedserver_sample":"true","virtualjdbc_sample":"true","ldap_sample":"true","lucenesearch_sample":"true","classificationsystems_sample":"true","solrfacetsearch_sample":"true","hmc_sample":"true","voucher_sample":"true","customerreview_sample":"true","homedepotcadataexport_sample":"true","wishlist_sample":"true","solrfacetsearchhmc_sample":"true","promotions_sample":"true","basecommerce_sample":"true","ticketsystem_sample":"true","cms2_sample":"true","btg_sample":"true","cms2lib_sample":"true","payment_sample":"true","platformhmc_sample":"true","commerceservices_sample":"true","commercewebservicescommons_sample":"true","acceleratorservices_sample":"true","acceleratorcms_sample":"true","homedepotcacybersource_sample":"true","homedepotcafulfilmentprocess_sample":"true","commercefacades_sample":"true","acceleratorfacades_sample":"true","addonsupport_sample":"true","acceleratorstorefrontcommons_sample":"true","b2ccheckoutaddon_sample":"true","commercesearch_sample":"true","commercesearchhmc_sample":"true","homedepotcacore_sample":"true","homedepotcafacades_sample":"true","certona_sample":"true","homedepotcacommercewebservices_sample":"true","homedepotcacommercewebservicestest_sample":"true","homedepotcatest_sample":"true","homedepotcastorefront_sample":"true","homedepotcainitialdata_sample":"true","cockpit_sample":"true","reportcockpit_sample":"true","cmscockpit_sample":"true","cscockpit_sample":"true","productcockpit_sample":"true","homedepotcacockpits_sample":"true","backoffice_sample":"true","commercesearchbackoffice_sample":"true","homedepotcabackoffice_sample":"true","lucenesearch_rebuild.indexes":["true"],"lucenesearch_update.index.configuration":["true"],"basecommerce_create geocoding cron job":["no"],"homedepotcacore_accessRights":["yes"],"homedepotcacommercewebservicestest_ImporthomedepotcaTestData":["yes"],"homedepotcatest_createTestData":["no"],"homedepotcastorefront_updateCmsUserRoles":["yes"],"homedepotcastorefront_updateCmsSite":["yes"],"homedepotcastorefront_importWcmsComponents":["yes"],"homedepotcainitialdata_importCoreData":["yes"],"homedepotcainitialdata_importSampleData":["no"],"homedepotcainitialdata_importSampleFeeds":["no"],"homedepotcainitialdata_importSampleProdClassFeed":["no"],"homedepotcainitialdata_importSampleMiniFeeds":["no"],"homedepotcainitialdata_importSampleCuratedFeeds":["no"],"homedepotcainitialdata_activateSolrCronJobs":["yes"],"homedepotcainitialdata_importSampleApplianceFeeds":["no"],"homedepotcainitialdata_importSampleProductCatalogs":["yes"],"homedepotcacockpits_importCustomReports":["yes"]},"initMethod":"INIT"}'

