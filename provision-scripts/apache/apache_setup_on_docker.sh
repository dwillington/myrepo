#export http_proxy=http://str-www-proxy2-qa.homedepot.com:8080
#export https_proxy=http://str-www-proxy2-qa.homedepot.com:8080

yum -y install mod_ssl openssl httpd curl

cd /opt
mkdir apache
mkdir dynatrace

cd /opt/apache
mkdir logs
mkdir html

cd /tmp

/bin/tar -zxvf homedepot-httpd-0.0.1-SNAPSHOT.tar.gz;

/bin/cp -r etc/* /etc/;
/bin/cp -r opt/apache/* /opt/apache/;
/bin/cp -r opt/dynatrace/* /opt/dynatrace/;
/bin/cp -r usr/lib64/httpd/modules/dispatcher-apache2.2-4.1.8.so  /usr/lib64/httpd/modules/dispatcher-apache2.2-4.1.8.so;

rm -rf /etc/httpd/conf/dispatcher-render-qp*
rm -rf /etc/httpd/conf/dispatcher-render-qa*
rm -rf /etc/httpd/conf/dispatcher-render-dev*
rm -rf /etc/httpd/conf/dispatcher-render-prod*
mv /etc/httpd/conf/dispatcher-render-docker-boot2docker.inc /etc/httpd/conf/dispatcher-render-qa-docker.inc
sed -i 's/${aem.publish.host}/epic1-aem/g' /etc/httpd/conf/dispatcher-render-qa-docker.inc
sed -i 's/`hostname -s`/*/g' /opt/apache/scripts/fetch-plp-map.sh
sed -i 's/`hostname -s`/*/g' /opt/apache/scripts/fetch-url-translation-map.sh
#sed -i 's/:4503/.homedepot.com:4503/g' /opt/apache/scripts/fetch-plp-map.sh
#sed -i 's/:4503/.homedepot.com:4503/g' /opt/apache/scripts/fetch-url-translation-map.sh

/bin/bash /opt/apache/scripts/init-apache.sh
/bin/rm -rf /tmp/etc /tmp/opt /tmp/usr;
/bin/chown -R root:apache /etc/httpd;
/bin/chown -R apache:apache /opt;
/bin/chmod +x /opt/apache/scripts/*;

apachectl start
