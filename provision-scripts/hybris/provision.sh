set INSTALL_REPOSITORY-/c/Temp/bamboo/data/thdutil/serversetup
export DESTINATION_HOST=ld4504

scp setup_base.sh root@$DESTINATION_HOST:/root/.
scp setup_env.sh root@$DESTINATION_HOST:/root/.
scp start_hybris.sh root@$DESTINATION_HOST:/root/.
scp stop_hybris.sh root@$DESTINATION_HOST:/root/.
ssh root@$DESTINATION_HOST chmod u+x /root/*.sh

scp local.properties root@$DESTINATION_HOST:/root/.

#ssh root@$DESTINATION_HOST /root/setup_base.sh
#scp $INSTALL_REPOSITORY/Solr/solr.tar root@ld4502:/opt/solr/.
#ssh root@$DESTINATION_HOST /root/setup_env.sh

