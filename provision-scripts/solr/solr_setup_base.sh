echo " "
echo "Connected to the Server"
echo " "

echo " "
echo "Switch to Base Directory"
echo " "

cd /

echo " "
echo "Create opt Directory"
echo " "

mkdir opt

echo " "
echo "Switch to the opt Directory"
echo " "

cd opt

echo " "
echo "Create Directory"
echo " "

mkdir solr

echo " "
echo "Switch to the Directory"
echo " "

cd solr

yum install unzip -y

iptables -A INPUT -i eth0 -p tcp --dport 80 -j ACCEPT
iptables -A INPUT -i eth0 -p tcp --dport 8080 -j ACCEPT
iptables -A PREROUTING -t nat -i eth0 -p tcp --dport 80 -j REDIRECT --to-port 8080
