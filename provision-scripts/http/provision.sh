export INSTALL_REPOSITORY=/bamboo/data/thdutil/serversetup/Apache
export DESTINATION_HOST=$1

scp apache_setup_base.sh root@$DESTINATION_HOST:/root/.
scp apache_setup_env.sh root@$DESTINATION_HOST:/root/.
ssh root@$DESTINATION_HOST chmod u+x /root/*.sh

scp $INSTALL_REPOSITORY/*.gz root@$DESTINATION_HOST:~/.

ssh root@$DESTINATION_HOST /root/apache_setup_base.sh
ssh root@$DESTINATION_HOST /root/apache_setup_env.sh
