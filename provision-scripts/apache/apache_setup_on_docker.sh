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

rm -rf /etc/httpd/conf/dispatcher-render-docker-qp.*
rm -rf /etc/httpd/conf/dispatcher-render-docker-qa.*
rm -rf /etc/httpd/conf/dispatcher-render-docker-dev.*
rm -rf /etc/httpd/conf/dispatcher-render-docker-prod.*
mv /etc/httpd/conf/dispatcher-render-docker-boot2docker.inc /etc/httpd/conf/dispatcher-render-qa-docker.inc
sed -i 's/${aem.publish.host}/ld5108/g' dispatcher-render-qa-docker.inc

/bin/bash /opt/apache/scripts/init-apache.sh
/bin/rm -rf /tmp/etc /tmp/opt /tmp/usr;
/bin/chown -R root:apache /etc/httpd;
/bin/chown -R apache:apache /opt/apache/scripts/*;
/bin/chown -R apache:apache /opt/apache/scripts
/bin/chown -R apache:apache /opt/apache/dynamicmaps
/bin/chown -R apache:apache /opt/apache/dynamicmaps/*;    
/bin/chmod +x /opt/apache/scripts/*;  

apachectl start
