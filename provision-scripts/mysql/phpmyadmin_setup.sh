#https://www.digitalocean.com/community/tutorials/how-to-install-and-secure-phpmyadmin-on-a-centos-6-4-vps
yum install httpd -y
yum install epel-release -y
yum install phpmyadmin -y
yum install php -y

#http://stackoverflow.com/questions/23235363/forbidden-you-dont-have-permission-to-access-phpmyadmin-on-this-server
# remove      Require ip 127.0.0.1
# remove      Require ip ::1
# add         Require all granted

sed -i '17s/^/#/' /etc/httpd/conf.d/phpMyAdmin.conf
sed -i '18s/^/#/' /etc/httpd/conf.d/phpMyAdmin.conf
sed -i '19i        Require all granted' /etc/httpd/conf.d/phpMyAdmin.conf

service httpd restart
