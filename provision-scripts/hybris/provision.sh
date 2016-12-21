set INSTALL_REPOSITORY=/bamboo/data/thdutil/serversetup/Hybris
export DESTINATION_HOST=$1

scp *.sh *.base *.pfx *.jck root@$DESTINATION_HOST:~/.

ssh root@$DESTINATION_HOST chmod u+x /root/*.sh

scp $INSTALL_REPOSITORY/*.* root@$DESTINATION_HOST:~/.

ssh root@$DESTINATION_HOST /root/hybris_setup_base.sh
ssh root@$DESTINATION_HOST /root/hybris_setup_env.sh

