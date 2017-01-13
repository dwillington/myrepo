export SSH_ARGS='-o StrictHostKeyChecking=no'
export INSTALL_REPOSITORY=/bamboo/data/thdutil/serversetup/Hybris
export DESTINATION_HOST=$1
#sshpass -p "password" ssh-copy-id root@$1

/bin/cp -rf local.properties.base.orig local.properties.base
export epic_name=${DESTINATION_HOST%-*}
sed -i -e "s/mysql-hostname/$epic_name-mysql/g" local.properties.base
echo "setting mysql hostname to $epic_name-mysql in local.properties.base"

scp $SSH_ARGS *.sh *.base *.pfx *.jck root@$DESTINATION_HOST:~/.

ssh $SSH_ARGS root@$DESTINATION_HOST chmod u+x /root/*.sh

scp $SSH_ARGS $INSTALL_REPOSITORY/*.* root@$DESTINATION_HOST:~/.

ssh $SSH_ARGS root@$DESTINATION_HOST /root/hybris_setup_base.sh
ssh $SSH_ARGS root@$DESTINATION_HOST /root/hybris_setup_env.sh

