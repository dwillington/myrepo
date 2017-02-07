echo "                "
echo "Extracting Java File"
echo "                "

cd /opt/solr

#done inside solar.tar
#tar -xvf jdk1.7.0_75.tar

echo "               "
echo "Start - Setting Java Base"
echo "               "

update-alternatives --install "/usr/bin/java" "java" "/opt/solr/jdk1.7.0_75/bin/java" 1
update-alternatives --install "/usr/bin/javac" "javac" "/opt/solr/jdk1.7.0_75/bin/javac" 1
update-alternatives --install "/usr/bin/javaws" "javaws" "/opt/solr/jdk1.7.0_75/bin/javaws" 1

echo "               "
echo "End - Setting Java Base"
echo "               "

echo " "
echo "Start - Extract file"
echo " "

tar -xvf /root/solr.tar.gz

echo " "
echo "End - Extract file"
echo " "

mkdir build
mkdir data
mkdir solr-home
mkdir solr-home_backup
ln -s apache-tomcat-7.0.68 tomcat

tar -zcvf /opt/solr/solr-home_backup/bk.`date +%m%d%Y`.tar.gz solr-home/
cd /opt/solr/build
tar -zxvf ~/homedepot-solr-0.0.1-SNAPSHOT.tar.gz
rm -rf /opt/solr/solr-home
mv /opt/solr/build/solr-home /opt/solr
#sleep 2m
/root/start_solr.sh
find /opt/solr/solr-home_backup/ -name "*.tar.gz" -mtime +7 -print -exec rm {} \;

tail -100 /opt/solr/tomcat/logs/catalina.out

