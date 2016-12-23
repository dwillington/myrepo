export INSTALL_REPOSITORY=/bamboo/data/thdutil/serversetup/MySQL
export DESTINATION_HOST=$1
sshpass -p "password" ssh-copy-id root@$1

scp mysql_setup_base.sh root@$DESTINATION_HOST:/root/.
scp mysql_setup_env.sh root@$DESTINATION_HOST:/root/.
scp *.sql root@$DESTINATION_HOST:/root/.
ssh root@$DESTINATION_HOST chmod u+x /root/*.sh

scp $INSTALL_REPOSITORY/*.rpm root@$DESTINATION_HOST:~/.

ssh root@$DESTINATION_HOST /root/mysql_setup_base.sh
ssh root@$DESTINATION_HOST /root/mysql_setup_env.sh
