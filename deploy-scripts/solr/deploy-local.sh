/etc/init.d/solr stop

cd /opt/solr/solr-configsets_backup
tar -zcvf bk.`date +%m%d%Y`.tar.gz /opt/solr/solr/server/solr/configsets

mkdir -p /opt/solr/build && cd /opt/solr/build
tar -zxvf /root/solr-configsets.tar.gz
rm -rf /opt/solr/solr/server/solr/configsets
cp -r /opt/solr/build/configsets /opt/solr/solr/server/solr
#mv /opt/solr/solr.in.sh /etc/default/.
rm -rf /opt/solr/build

echo "--------------------------------------------------"
echo "sleeping 2m while waiting for solr to stop..."
echo "--------------------------------------------------"
/etc/init.d/solr start
find /opt/solr/solr-configsets_backup/ -name "*.tar.gz" -mtime +7 -print -exec rm {} \;
tail -100 /opt/solr/homedepot-solr/server/logs/solr.log
