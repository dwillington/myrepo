export SSH_ARGS='-o StrictHostKeyChecking=no'

if [ $# -lt 1 ]; then
  echo 1>&2 "$0: missing epic_name"
  exit 2
fi

gsutil rsync -d -r gs://np-cadotcom.appspot.com/ci-builds/epic-builds/ /tmp/epic-builds
export ARTIFACT_LOCATION=/tmp/epic-builds/$1/hybris
export DESTINATION_HOST=$1-hybris

# http://stackoverflow.com/questions/90418/exit-shell-script-based-on-process-exit-code
#  use this technique for error checking exit code
ls -al $ARTIFACT_LOCATION/hybrisServer-Platform.zip
ret=$? && if [ $ret -ne 0 ]; then exit $ret; fi
ls -al $ARTIFACT_LOCATION/hybrisServer-AllExtensions.zip
ret=$? && if [ $ret -ne 0 ]; then exit $ret; fi
ls -al $ARTIFACT_LOCATION/hybrisServer-Config.zip
ret=$? && if [ $ret -ne 0 ]; then exit $ret; fi

scp $SSH_ARGS $ARTIFACT_LOCATION/*.zip root@$DESTINATION_HOST:/root/.
scp $SSH_ARGS deploy-local.sh root@$DESTINATION_HOST:/root/.

ssh $SSH_ARGS root@$DESTINATION_HOST /root/deploy-local.sh