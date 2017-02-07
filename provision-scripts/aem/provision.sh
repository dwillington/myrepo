if [ $# -lt 1 ]; then
  echo 1>&2 "$0: missing epic_name"
  exit 2
fi

export SSH_ARGS='-o StrictHostKeyChecking=no'
export INSTALL_REPOSITORY=/bamboo/data/thdutil/serversetup/Aem
export DESTINATION_HOST=$1

scp $SSH_ARGS aem_setup_base.sh root@$DESTINATION_HOST:/root/.
scp $SSH_ARGS aem_setup_env.sh root@$DESTINATION_HOST:/root/.
scp $SSH_ARGS aem_setup_publish.sh root@$DESTINATION_HOST:/root/.
ssh $SSH_ARGS root@$DESTINATION_HOST chmod u+x /root/*.sh

scp $SSH_ARGS $INSTALL_REPOSITORY/aem.tar.gz root@$DESTINATION_HOST:~/.

ssh $SSH_ARGS root@$DESTINATION_HOST /root/aem_setup_base.sh
ssh $SSH_ARGS root@$DESTINATION_HOST /root/aem_setup_env.sh
ssh $SSH_ARGS root@$DESTINATION_HOST /root/aem_setup_publish.sh