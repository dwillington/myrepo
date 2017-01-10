export SSH_ARGS='-o StrictHostKeyChecking=no'
export INSTALL_REPOSITORY=/bamboo/data/thdutil/serversetup/Solr
export DESTINATION_HOST=$1
#sshpass -p "password" ssh-copy-id root@$1

scp $SSH_ARGS *.sh root@$DESTINATION_HOST:/root/.
ssh $SSH_ARGS root@$DESTINATION_HOST chmod u+x /root/*.sh

scp $SSH_ARGS $INSTALL_REPOSITORY/solr.tar root@$DESTINATION_HOST:/root/.
scp $SSH_ARGS $INSTALL_REPOSITORY/*.tar.gz root@$DESTINATION_HOST:/root/.

ssh $SSH_ARGS root@$DESTINATION_HOST /root/solr_setup_base.sh
ssh $SSH_ARGS root@$DESTINATION_HOST /root/solr_setup_env.sh