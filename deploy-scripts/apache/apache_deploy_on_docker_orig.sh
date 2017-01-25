apachectl stop

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
mv /etc/httpd/conf/dispatcher-render-docker-boot2docker.inc /etc/httpd/conf/dispatcher-render-dev-docker.inc
sed -i 's/${aem.publish.host}/epic_name-aem/g' /etc/httpd/conf/dispatcher-render-dev-docker.inc
sed -i 's/ln0a76/epic_name-hybris/g' /etc/httpd/conf/vhost-a80.conf
sed -i 's/ln0a76/epic_name-hybris/g' /etc/httpd/conf/vhost-z443.conf
sed -i 's/ln0a76/epic_name-hybris/g' /etc/httpd/conf/vhost-x8888.conf
sed -i 's/amphdcaservices-dev.homedepot.ca/epic_name-hybris/g' /etc/httpd/conf/vhost-a80.conf
sed -i 's/amphdcaservices-dev.homedepot.ca/epic_name-hybris/g' /etc/httpd/conf/vhost-z443.conf
sed -i 's/`hostname -s`/*/g' /opt/apache/scripts/fetch-plp-map.sh
sed -i 's/`hostname -s`/*/g' /opt/apache/scripts/fetch-url-translation-map.sh

/bin/bash /opt/apache/scripts/init-apache.sh
/bin/rm -rf /tmp/etc /tmp/opt /tmp/usr;
/bin/chown -R root:apache /etc/httpd;
/bin/chown -R apache:apache /opt;
/bin/chmod +x /opt/apache/scripts/*;

apachectl start

 
