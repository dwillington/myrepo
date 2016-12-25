echo " "
echo "Starting MySQL Setup"
echo " "

yum -y remove mariadb-libs
yum -y remove MariaDB-common

cd /opt/mysql

rpm -ivh ~/MySQL-shared-5.5.30-1.el6.x86_64.rpm

yum install libaio -y
rpm -ivh ~/MySQL-client-5.5.30-1.el6.x86_64.rpm

yum install net-tools -y
rpm -ivh ~/MySQL-server-5.5.30-1.el6.x86_64.rpm

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

mysql -uroot -ppassword < ~/sonar-setup.sql
mysql -uroot -ppassword < ~/hybris-setup.sql
