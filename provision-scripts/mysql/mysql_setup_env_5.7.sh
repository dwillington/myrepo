echo " "
echo "Starting MySQL Setup"
echo " "

yum install wget -y

cd /opt/mysql

wget https://dev.mysql.com/get/mysql57-community-release-el7-9.noarch.rpm
rpm -ivh mysql57-community-release-el7-9.noarch.rpm
yum install mysql-server
systemctl start mysqld

echo " "
echo "Setting mysql root password"
echo " "

export mysql_password="`grep 'temporary.*root@localhost' /var/log/mysqld.log | sed 's/.*root@localhost: //'`"
mysql -uroot -p$mysql_password --init-command='set password for root@localhost = password("Jasp3r9!")'

echo " "
echo "Ending MySQL Setup"
echo " "

echo " "
echo "adding mysql schemas"
echo " "

mysql -uroot -pJasp3r9! < ~/sonar-setup.sql
mysql -uroot -pJasp3r9! < ~/hybris-setup.sql
