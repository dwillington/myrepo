export INSTALL_REPOSITORY=/bamboo/data/thdutil/serversetup/Aem
export DESTINATION_HOST=$1
#sshpass -p "password" ssh-copy-id root@$1

scp aem_setup_base.sh root@$DESTINATION_HOST:/root/.
scp aem_setup_env.sh root@$DESTINATION_HOST:/root/.
scp aem_setup_publish.sh root@$DESTINATION_HOST:/root/.
ssh root@$DESTINATION_HOST chmod u+x /root/*.sh

#scp $INSTALL_REPOSITORY/aem.tar.gz root@$DESTINATION_HOST:~/.

ssh root@$DESTINATION_HOST /root/aem_setup_base.sh
ssh root@$DESTINATION_HOST /root/aem_setup_env.sh
ssh root@$DESTINATION_HOST /root/aem_setup_publish.sh