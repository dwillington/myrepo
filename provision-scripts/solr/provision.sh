export INSTALL_REPOSITORY=/bamboo/data/thdutil/serversetup
export DESTINATION_HOST=ld4502

scp *.sh root@$DESTINATION_HOST:/root/.
ssh root@$DESTINATION_HOST chmod u+x /root/*.sh

scp $INSTALL_REPOSITORY/Solr/solr.tar root@$DESTINATION_HOST:/root/.
scp $INSTALL_REPOSITORY/Solr/homedepot-solr-.*.tar.gz root@$DESTINATION_HOST:/root/.

ssh root@$DESTINATION_HOST /root/solr_setup_base.sh
ssh root@$DESTINATION_HOST /root/solr_setup_env.sh

