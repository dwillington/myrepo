/root/stop_solr.sh
tar -zcvf /opt/solr/solr-home_backup/bk.`date +%m%d%Y`.tar.gz solr-home/
cd /opt/solr/build
tar -zxvf /root/homedepot-solr-0.0.1-SNAPSHOT.tar.gz
rm -rf /opt/solr/solr-home
mv /opt/solr/build/solr-home /opt/solr
echo "--------------------------------------------------"
echo "sleeping 2m while waiting for solr to stop..."
echo "--------------------------------------------------"
sleep 2m
/root/start_solr.sh
find /opt/solr/solr-home_backup/ -name "*.tar.gz" -mtime +7 -print -exec rm {} \;

tail -100 /opt/solr/tomcat/logs/catalina.out
