export SSH_ARGS='-o StrictHostKeyChecking=no'
export INSTALL_REPOSITORY=/bamboo/data/thdutil/serversetup/Solr-6.1

if [ $# -lt 1 ]; then
  echo 1>&2 "$0: missing hostname"
  exit 2
fi

export DESTINATION_HOST=$1

scp $SSH_ARGS *.sh root@$DESTINATION_HOST:/root/.
ssh $SSH_ARGS root@$DESTINATION_HOST chmod u+x /root/*.sh

scp $SSH_ARGS $INSTALL_REPOSITORY/solr.tar.gz root@$DESTINATION_HOST:/root/.

ssh $SSH_ARGS root@$DESTINATION_HOST /root/solr_setup_base.sh
ssh $SSH_ARGS root@$DESTINATION_HOST /root/solr_setup_env.sh