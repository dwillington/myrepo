#/bin/sh
#yum install inotify-tools -y

if [ $# -lt 2 ]; then
  echo 1>&2 "$0: must provide epic_name and project_name"
  exit 2
fi

export SLEEP_PERIOD=2

export epic_name=$1
export project_name=$2

# export epic_name=epic1
# export project_name=solr

export file="/tmp/epic-deploy-results/$epic_name/$project_name/deploy.result"
rm -rf $file

x=0
while [ "$x" -lt 30 -a ! -e $file ]; do
    mkdir -p /tmp/epic-deploy-results
    /root/google-cloud-sdk/bin/gsutil rsync -d -r gs://np-cadotcom.appspot.com/ci-builds/epic-deploy-results/ /tmp/epic-deploy-results
    x=$((x+1))
    #http://stackoverflow.com/questions/2379829/while-loop-to-test-if-a-file-exists-in-bash
    inotifywait -qqt $SLEEP_PERIOD -e create -e moved_to "$(dirname $file)"
done
if [ -e $file ]
then
    echo "Found: $file"
        #http://stackoverflow.com/questions/4749330/how-to-test-if-string-exists-in-file-with-bash-shell
        if grep -Fq "SUCCESS" $file
        then
            echo "SUCCESS"
            exit 0
        else
            echo "FAILURE"
            exit 1
        fi
else
    echo "File $file not found within time limit!"
    exit 1
fi
