ip6tables -A INPUT -i eth0 -p tcp --dport 80 -j ACCEPT
ip6tables -A INPUT -i eth0 -p tcp --dport 3306 -j ACCEPT
ip6tables -A PREROUTING -t nat -i eth0 -p tcp --dport 80 -j REDIRECT --to-port 3306

iptables -D INPUT -i eth0 -p tcp --dport 80 -j ACCEPT
iptables -D INPUT -i eth0 -p tcp --dport 3306 -j ACCEPT
iptables -D PREROUTING -t nat -i eth0 -p tcp --dport 80 -j REDIRECT --to-port 3306

iptables -A INPUT -i eth0 -p tcp --dport 80 -j ACCEPT
iptables -A INPUT -i eth0 -p tcp --dport 3306 -j ACCEPT
iptables -A PREROUTING -t nat -i eth0 -p tcp --dport 80 -j REDIRECT --to-port 3306

iptables -t nat -L

iptables -t nat -A PREROUTING -p tcp -d 0.0.0.0 --dport 80 -j DNAT --to-destination 127.0.0.1:3306


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
mysql --connect-expired-password -uroot -p$mysql_password < /root/set_initial_password.sql

echo " "
echo "Ending MySQL Setup"
echo " "

echo " "
echo "adding mysql schemas"
echo " "

mysql -uroot -pjasp3r91 < /root/sonar-setup.sql
mysql -uroot -pjasp3r91 < /root/hybris-setup.sql
