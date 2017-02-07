if [ $# -lt 1 ]; then
  echo 1>&2 "$0: missing hostname"
  exit 2
fi

export SSH_ARGS='-o StrictHostKeyChecking=no'
export INSTALL_REPOSITORY=/bamboo/data/thdutil/serversetup/MySQL
export DESTINATION_HOST=$1

scp $SSH_ARGS mysql_setup_base.sh root@$DESTINATION_HOST:/root/.
scp $SSH_ARGS mysql_setup_env_5.7.sh root@$DESTINATION_HOST:/root/.
scp $SSH_ARGS sonar_setup.sh root@$DESTINATION_HOST:/root/.
scp $SSH_ARGS *.sql root@$DESTINATION_HOST:/root/.
ssh $SSH_ARGS root@$DESTINATION_HOST chmod u+x /root/*.sh

#scp $SSH_ARGS $INSTALL_REPOSITORY/*.rpm root@$DESTINATION_HOST:~/.

ssh $SSH_ARGS root@$DESTINATION_HOST /root/mysql_setup_base.sh
ssh $SSH_ARGS root@$DESTINATION_HOST /root/mysql_setup_env_5.7.sh
#ssh $SSH_ARGS root@$DESTINATION_HOST /root/sonar_setup.sh
