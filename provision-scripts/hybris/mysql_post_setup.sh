export my_host_name=`hostname`
export epic_name=${my_host_name%-*}

echo "use hybrisdbuser;" > /root/solrendpointurl.sql
echo "update solrendpointurl set p_url=\"http://$epic_name-solr/solr\" where p_solrserverconfig=(SELECT pk FROM solrserverconfig where p_name='homedepotcaSolrServerConfig');" >> /root/solrendpointurl.sql
mysql -uroot -pjasp3r91 < /root/solrendpointurl.sql