#/bin/sh
export http_proxy=http://str-www-proxy2-qa.homedepot.com:8080
export https_proxy=http://str-www-proxy2-qa.homedepot.com:8080

if [ $# -lt 2 ]; then
  echo 1>&2 "$0: provide random_folder"
  exit 2
fi

export SLEEP_PERIOD=5
#export random_folder=delete-me-$RANDOM
export random_folder=$1

grep Location /tmp/$random_folder/out.txt.orig > /tmp/$random_folder/out.txt
sed -i 's/Location: //' /tmp/$random_folder/out.txt
dos2unix /tmp/$random_folder/out.txt
sed -i 's/$/api\/json?pretty=true/' /tmp/$random_folder/out.txt
export queue_item_url=`cat /tmp/$random_folder/out.txt`

curl -H "$CRUMB" \
     --user admin:76a4a60136ff3f563f7ad5c3fd52552d \
     -X POST $queue_item_url > /tmp/$random_folder/queue_item.txt

# from here you can parse out url, see output below
export PYTHONIOENCODING=utf8
cat /tmp/$random_folder/queue_item.txt | python -c "import sys, json; print json.load(sys.stdin)['executable']['url']" > /tmp/$random_folder/out.txt
sed -i 's/$/api\/json?pretty=true/' /tmp/$random_folder/out.txt
deploy_job_url=`cat /tmp/$random_folder/out.txt`
echo $deploy_job_url

while [ "$x" -lt 30 ]; do
    curl -H "$CRUMB" \
        --user admin:76a4a60136ff3f563f7ad5c3fd52552d \
        -X POST $deploy_job_url > /tmp/$random_folder/deploy_job.txt
    deploy_job_result=`cat /tmp/$random_folder/deploy_job.txt | python -c "import sys, json; print json.load(sys.stdin)['result']"`
    echo $deploy_job_result
    if [ "$deploy_job_result" == "SUCCESS" ]; then
       echo "success"
       break
    fi
    if [ "$deploy_job_result" == "FAILURE" ]; then
       echo "failure"
       break
    fi
    sleep $SLEEP_PERIOD
    x=$((x+1))
done

if [ "$deploy_job_result" == "SUCCESS" ]; then
   exit 0;
else
   echo "failure / gave up"
   exit 1;
fi
