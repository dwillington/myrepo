echo " "
echo "Starting MySQL Setup"
echo " "

yum -y remove mariadb-libs
yum -y remove MariaDB-common
yum install libaio net-tools -y

cd /opt/mysql

rpm -ivh /root/MySQL-shared-5.6.16-1.el6.x86_64.rpm
rpm -ivh /root/MySQL-client-5.6.16-1.el6.x86_64.rpm
rpm -ivh /root/MySQL-server-5.6.16-1.el6.x86_64.rpm

service mysql start

echo " "
echo "Setting mysql root password"
echo " "

export mysql_password=`cat ~/.mysql_secret |grep random|cut -d: -f4-|sed 's| ||g'|tail -1` 
mysql -uroot -p$mysql_password --init-command='set password for root@localhost = password("password")'

echo " "
echo "Ending MySQL Setup"
echo " "

echo " "
echo "adding mysql schemas"
echo " "

mysql -uroot -ppassword < ~/sonar-setup.sql
mysql -uroot -ppassword < ~/hybris-setup.sql
