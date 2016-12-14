set INSTALL_REPOSITORY=/bamboo/data/thdutil/serversetup
export DESTINATION_HOST=ld4502

scp solr_setup_base.sh root@$DESTINATION_HOST:/root/.
scp solr_setup_env.sh root@$DESTINATION_HOST:/root/.
ssh root@$DESTINATION_HOST chmod u+x /root/*.sh

ssh root@$DESTINATION_HOST /root/solr_setup_base.sh
scp $INSTALL_REPOSITORY/Solr/solr.tar root@$DESTINATION_HOST:/opt/solr/.
scp $INSTALL_REPOSITORY/Solr/homedepot-solr-.*.tar.gz root@$DESTINATION_HOST:/opt/solr/.
ssh root@$DESTINATION_HOST /root/solr_setup_env.sh

