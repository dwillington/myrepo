export DESTINATION_HOST=`hostname`
export epic_name=${DESTINATION_HOST%-*}

echo "uppdate solrendpointurl set p_url=\"http://$epic_name-solr/solr\" where p_solrserverconfig=(SELECT pk FROM solrserverconfig where p_name='homedepotcaSolrServerConfig');" > /root/solrendpointurl.sql
mysql -uroot -pjasp3r91 < /root/solrendpointurl.sql