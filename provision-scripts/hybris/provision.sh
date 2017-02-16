export SSH_ARGS='-o StrictHostKeyChecking=no'
export INSTALL_REPOSITORY=/bamboo/data/thdutil/serversetup/Hybris-6.2
export DESTINATION_HOST=$1

scp $SSH_ARGS *.sh *.orig *.txt root@$DESTINATION_HOST:~/.

ssh $SSH_ARGS root@$DESTINATION_HOST chmod u+x /root/*.sh

scp $SSH_ARGS $INSTALL_REPOSITORY/*.* root@$DESTINATION_HOST:~/.

ssh $SSH_ARGS root@$DESTINATION_HOST /root/hybris_setup_base.sh
ssh $SSH_ARGS root@$DESTINATION_HOST /root/hybris_setup_env_6.2.sh

