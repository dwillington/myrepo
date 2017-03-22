if [ $# -lt 1 ]; then
  echo 1>&2 "$0: missing hostname"
  exit 2
fi

export SSH_ARGS='-o StrictHostKeyChecking=no'
export INSTALL_REPOSITORY=/bamboo/data/thdutil/serversetup/mountebank-v1.8.0
export DESTINATION_HOST=$1

scp $SSH_ARGS *.sh root@$DESTINATION_HOST:/root/.

ssh $SSH_ARGS root@$DESTINATION_HOST chmod u+x /root/*.sh

scp -r $SSH_ARGS $INSTALL_REPOSITORY root@$DESTINATION_HOST:/root/.

ssh $SSH_ARGS root@$DESTINATION_HOST /root/ip_tables_setup.sh
ssh $SSH_ARGS root@$DESTINATION_HOST /root/mountebank_setup_env.sh