if [ $# -lt 1 ]; then
  echo 1>&2 "$0: missing hostname"
  exit 2
fi

export SSH_ARGS='-o StrictHostKeyChecking=no'
export INSTALL_REPOSITORY=/bamboo/data/thdutil/serversetup/Hybris-6.2
export DESTINATION_HOST=$1

scp $SSH_ARGS *.sh *.orig *.base *.txt root@$DESTINATION_HOST:~/.

ssh $SSH_ARGS root@$DESTINATION_HOST chmod u+x /root/*.sh

scp $SSH_ARGS $INSTALL_REPOSITORY/*.* root@$DESTINATION_HOST:~/.

# the following will stage a pre built hybrisServer-{AllExtensions, Config, Licence, Platform}.zip
gsutil rsync -d -r gs://np-cadotcom.appspot.com/ci-builds/epic-builds/ /tmp/epic-builds
export epic_name=${DESTINATION_HOST%-*}
export ARTIFACT_LOCATION=/tmp/epic-builds/$epic_name/hybris
scp $SSH_ARGS $ARTIFACT_LOCATION/*.zip root@$DESTINATION_HOST:/root/.

ssh $SSH_ARGS root@$DESTINATION_HOST /root/hybris_setup_base.sh
ssh $SSH_ARGS root@$DESTINATION_HOST /root/hybris_setup_env_6.2.sh

