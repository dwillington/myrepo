echo "                "
echo "Extracting Java File"
echo "                "

cd /opt/jdk
tar xvf ~/jdk1.7.0_75.tar

echo "               "
echo "Start - Setting Java Base"
echo "               "

update-alternatives --install "/usr/bin/java" "java" "/opt/jdk/jdk1.7.0_75/bin/java" 1
update-alternatives --install "/usr/bin/javac" "javac" "/opt/jdk/jdk1.7.0_75/bin/javac" 1
update-alternatives --install "/usr/bin/javaws" "javaws" "/opt/jdk/jdk1.7.0_75/bin/javaws" 1

echo "               "
echo "End - Setting Java Base"
echo "               "

cd /opt/hybris

echo " "
echo "Start - Extract file"
echo " "

unzip ~/HYBRISCOMM5400_0.ZIP

echo " "
echo "End - Extract file"
echo " "

cd /opt/hybris/hybris/bin/platform
chmod 755 *.sh
. ./setantenv.sh
printf '\n' | ant clean all

# this below represents deploy, ant clean all
rm -rf /opt/hybris/hybris/bin
/usr/bin/unzip -o /root/hybrisServer-Platform.zip -d /opt/hybris/
/usr/bin/unzip -o /root/hybrisServer-AllExtensions.zip -d /opt/hybris/
/usr/bin/unzip -o /root/hybrisServer-Config.zip -d /opt/hybris/

cd /opt/hybris/hybris/config
rm -rf local.properties
cp -p /root/local.properties.base local.properties
#/bin/cp -rf ~/local.properties /opt/hybris/hybris/config/.
/bin/cp -rf ~/mysql-connector-java-5.1.35-bin.jar /opt/hybris/hybris/bin/platform/lib/dbdriver/.

cp -p ~/QA_PLCC-hdca.jck /opt/hybris/security/
cp -p ~/HD.ca-BankToken-QA.pfx /opt/hybris/security/

cd /opt/hybris/hybris/bin/platform
. ./setantenv.sh
ant initialize > out.txt

cd /opt/hybris/hybris/bin/platform/tomcat/conf
rm -rf wrapper.conf
cp -p /root/wrapper.conf.base wrapper.conf

sh ~/start_hybris.sh

ls -al /opt/hybris/hybris/log/tomcat
tail -100 /opt/hybris/hybris/bin/platform/tomcat/logs/console-*.log
tail -100 /opt/hybris/hybris/log/tomcat/console-*.log