echo " "
echo "Start - Setting Publish Environment"
echo " "

echo "Setting Environment"

cd /opt/adobe/publish

sed -i -e 's/3072/4096/g' setenv.sh

. ./setenv.sh 

echo $CQ_JVM_OPTS
echo $CQ_RUNMODE
echo $CQ_PORT

echo "Starting the Instance"

cd /opt/adobe/publish/crx-quickstart/bin

./start

tail -100 /opt/adobe/publish/crx-quickstart/logs/stdout.log

timeout 120 sed '/Server completed/q' <(tail -n 0 -f /opt/adobe/publish/crx-quickstart/logs/stdout.log)

curl -u admin:admin "http://localhost:4503/system/console/configMgr/ca.homedepot.aem.services.impl.SceneSevenServiceImpl" --data "apply=true&action=ajaxConfigManager&homedepot.s7.base-url=https%3A%2F%2Fimages.homedepot.ca&homedepot.s7.image-server=is%2Fimage&homedepot.s7.account-name=homedepotcanada&propertylist=homedepot.s7.base-url%2Chomedepot.s7.image-server%2Chomedepot.s7.account-name"

export my_host_name=`hostname`
export epic_name=${my_host_name%-*}
curl -u admin:admin 'http://localhost:4503/system/console/configMgr/ca.homedepot.cq.commerce.hybris.common.HDHybrisConnection' \
--data "apply=true&action=ajaxConfigManager&service.ranking=1201&hybris.server.url=http%3A%2F%2F$epic_name-hybris%3A9001&hybris.server.username=admin&hybris.server.password=nimda&hybris.server.ssl.trust_all_certs=true&hybris.server.ssl.trust_all_certs=false&hybris.server.store_independent_prefixes=%2Fws410%2Frest&hybris.server.store_independent_prefixes=%2Fmedias&hybris.server.rest_endpoint=%2Fhomedepotcacommercewebservices%2Fv2%2F&hybris.http.timeout.connection=30000&hybris.http.timeout.socket=30000&hybris.http.max_connections=200&hybris.http.max_connections_per_host=50&hybris.proxy.enabled=false&hybris.proxy.host=localhost&hybris.proxy.port=8080&propertylist=service.ranking%2Chybris.server.url%2Chybris.server.username%2Chybris.server.password%2Chybris.server.ssl.trust_all_certs%2Chybris.server.store_independent_prefixes%2Chybris.server.rest_endpoint%2Chybris.http.timeout.connection%2Chybris.http.timeout.socket%2Chybris.http.max_connections%2Chybris.http.max_connections_per_host%2Chybris.proxy.enabled%2Chybris.proxy.host%2Chybris.proxy.port"
