echo "                "
echo "Extracting Java File"
echo "                "
yum install lsof -y

cd /

echo " "
echo "Start - Extract file"
echo " "

tar -zxvf /root/solr.tar.gz

echo " "
echo "End - Extract file"
echo " "

echo "               "
echo "Start - Setting Java Base"
echo "               "

update-alternatives --install "/usr/bin/java" "java" "/opt/solr/jdk1.8.0_111/bin/java" 1
update-alternatives --install "/usr/bin/javac" "javac" "/opt/solr/jdk1.8.0_111/bin/javac" 1
update-alternatives --install "/usr/bin/javaws" "javaws" "/opt/solr/jdk1.8.0_111/bin/javaws" 1

echo "               "
echo "End - Setting Java Base"
echo "               "

mv /opt/solr/solr.in.sh /etc/default/.

mkdir -p /opt/solr/build && cd /opt/solr/build
tar -zxvf /root/solr-configsets.tar.gz
rm -rf /opt/solr/solr/server/solr/configsets
cp -r /opt/solr/build/configsets /opt/solr/solr/server/solr
rm -rf /opt/solr/build

cd /opt/solr
chmod a+x install_solr_service.sh 
./install_solr_service.sh homedepot-solr.tgz -f -i /opt/solr -d /opt/solr -u root -s solr -p 8080

tail -100 /opt/solr/homedepot-solr/server/logs/solr.log