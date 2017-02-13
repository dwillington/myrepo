export SSH_ARGS='-o StrictHostKeyChecking=no'
export INSTALL_REPOSITORY=/bamboo/data/thdutil/serversetup/Hybris-6.2
export DESTINATION_HOST=$1

#http://stackoverflow.com/questions/4168371/how-can-i-remove-all-text-after-a-character-in-bash
#http://stackoverflow.com/questions/16297052/replace-a-text-with-a-variable-sed
/bin/cp -rf local.properties.base.orig local.properties.base
export epic_name=${DESTINATION_HOST%-*}
sed -i -e "s/mysql-hostname/$epic_name-mysql/g" local.properties.base
echo "setting mysql hostname to $epic_name-mysql in local.properties.base"

scp $SSH_ARGS *.sh *.base root@$DESTINATION_HOST:~/.

ssh $SSH_ARGS root@$DESTINATION_HOST chmod u+x /root/*.sh

scp $SSH_ARGS $INSTALL_REPOSITORY/*.* root@$DESTINATION_HOST:~/.

ssh $SSH_ARGS root@$DESTINATION_HOST /root/hybris_setup_base.sh
ssh $SSH_ARGS root@$DESTINATION_HOST /root/hybris_setup_env_6.2.sh

