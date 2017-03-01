echo " "
echo "Starting MySQL Setup"
echo " "

yum -y remove mariadb-libs
yum -y remove MariaDB-common
yum install libaio net-tools -y

cd /opt/mysql

rpm -ivh /root/MySQL-shared-5.5.30-1.el6.x86_64.rpm

rpm -ivh /root/MySQL-client-5.5.30-1.el6.x86_64.rpm

rpm -ivh /root/MySQL-server-5.5.30-1.el6.x86_64.rpm

service mysql start

echo " "
echo "Setting mysql root password"
echo " "

/usr/bin/mysqladmin -u root password 'password'

echo " "
echo "Ending MySQL Setup"
echo " "

echo " "
echo "adding mysql schemas"
echo " "

mysql -uroot -ppassword < /root/sonar-setup.sql
mysql -uroot -ppassword < /root/hybris-setup.sql
