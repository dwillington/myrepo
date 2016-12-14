export INSTALL_REPOSITORY=/bamboo/data/thdutil/serversetup
export DESTINATION_HOST=ld4505

scp mysql_setup_base.sh root@$DESTINATION_HOST:/root/.
scp mysql_setup_env.sh root@$DESTINATION_HOST:/root/.
scp $INSTALL_REPOSITORY/*.sql root@$DESTINATION_HOST:/root/.
scp $INSTALL_REPOSITORY/*.rpm root@$DESTINATION_HOST:~/.
ssh root@$DESTINATION_HOST chmod u+x /root/*.sh

ssh root@$DESTINATION_HOST /root/mysql_setup_base.sh
ssh root@$DESTINATION_HOST /root/mysql_setup_env.sh
