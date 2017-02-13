iptables -A INPUT -i eth0 -p tcp --dport 80 -j ACCEPT
iptables -A INPUT -i eth0 -p tcp --dport 9001 -j ACCEPT
iptables -A PREROUTING -t nat -i eth0 -p tcp --dport 80 -j REDIRECT --to-port 9001

iptables -A INPUT -i eth0 -p tcp --dport 443 -j ACCEPT
iptables -A INPUT -i eth0 -p tcp --dport 9002 -j ACCEPT
iptables -A PREROUTING -t nat -i eth0 -p tcp --dport 443 -j REDIRECT --to-port 9002

yum install unzip -y

cd /opt
mkdir hybris
mkdir jdk

cd hybris
mkdir build

cd /opt/jdk

tar xvf ~/jdk1.8.0_111.tar

update-alternatives --install "/usr/bin/java" "java" "/opt/jdk/jdk1.8.0_111/bin/java" 1
update-alternatives --install "/usr/bin/javac" "javac" "/opt/jdk/jdk1.8.0_111/bin/javac" 1
update-alternatives --install "/usr/bin/javaws" "javaws" "/opt/jdk/jdk1.8.0_111/bin/javaws" 1

cd /opt/hybris

mkdir hybris

cd hybris

unzip ~/hybris_6_2_0_0.zip

rm -rf /opt/hybris/hybris/bin/platform 
rm -rf /opt/hybris/hybris/bin/custom 

/usr/bin/unzip -o /root/hybrisServer-Platform.zip -d /opt/hybris/
/usr/bin/unzip -o /root/hybrisServer-AllExtensions.zip hybris/bin/custom/* -d /opt/hybris/
/usr/bin/unzip -o /root/hybrisServer-Config.zip -d /opt/hybris/

cd /opt/hybris/hybris/config
rm -rf local.properties
cp -p /root/local.properties.base local.properties

/bin/cp -rf ~/mysql-connector-java-5.1.40-bin.jar /opt/hybris/hybris/bin/platform/lib/dbdriver/.

cd /opt/hybris/hybris/bin/platform
chmod 755 *.sh
. ./setantenv.sh
printf '\n' | ant clean all

cd /opt/hybris/hybris/bin/platform/tomcat/conf
rm -rf wrapper.conf
cp -p /root/wrapper.conf.base wrapper.conf

cd /opt/hybris/hybris/bin/platform/
. ./setantenv.sh
ant initialize

export my_public_ip_address=`curl ipinfo.io/ip`

/opt/jdk/jdk1.8.0_111/bin/keytool -genkey -dname "CN=$my_public_ip_address, OU=I, O=I, L=T, ST=On, C=CA" -alias cloudhybris -validity 3650 -keyalg RSA -keystore /opt/hybris/hybris/bin/platform/tomcat/lib/keystore -storepass 123456 