echo " "
echo "Starting MySQL Setup"
echo " "

yum install wget -y

wget https://dev.mysql.com/get/mysql57-community-release-el7-9.noarch.rpm
rpm -ivh mysql57-community-release-el7-9.noarch.rpm
yum install mysql-server -y
systemctl start mysqld

echo " "
echo "Setting mysql root password"
echo " "

export mysql_password="`grep 'temporary.*root@localhost' /var/log/mysqld.log | sed 's/.*root@localhost: //'`"
mysql -uroot -p$mysql_password < /root/set_inital_password.sql
#mysql -uroot -pJasp3r9! < /root/set_inital_password.sql

echo " "
echo "Ending MySQL Setup"
echo " "

echo " "
echo "adding mysql schemas"
echo " "

mysql -uroot -pJasp3r9! < /root/sonar-setup.sql
mysql -uroot -pJasp3r9! < /root/hybris-setup.sql
