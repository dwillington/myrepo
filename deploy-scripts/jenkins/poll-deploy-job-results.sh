#/bin/sh
export http_proxy=http://str-www-proxy2-qa.homedepot.com:8080
export https_proxy=http://str-www-proxy2-qa.homedepot.com:8080

if [ $# -lt 2 ]; then
  echo 1>&2 "$0: must provide epic_name and project_name"
  exit 2
fi

export epic_name=$1
export project_name=$2

export SLEEP_PERIOD=5
random_folder=/tmp/delete-me-$epic_name-$project_name

# get queue_item_url
grep Location $random_folder/out.txt.orig > $random_folder/out.txt
sed -i 's/Location: //' $random_folder/out.txt
dos2unix $random_folder/out.txt
sed -i 's/$/api\/json?pretty=true/' $random_folder/out.txt
export queue_item_url=`cat $random_folder/out.txt`

# get queue_item_json
curl -H "$CRUMB" \
     --user admin:76a4a60136ff3f563f7ad5c3fd52552d \
     -X POST $queue_item_url > $random_folder/queue_item.txt

# parse out deploy_job-url
export PYTHONIOENCODING=utf8
cat $random_folder/queue_item.txt | python -c "import sys, json; print json.load(sys.stdin)['executable']['url']" > $random_folder/out.txt
sed -i 's/$/api\/json?pretty=true/' $random_folder/out.txt
deploy_job_url=`cat $random_folder/out.txt`
echo $deploy_job_url

while [ "$x" -lt 30 ]; do
    curl -H "$CRUMB" \
        --user admin:76a4a60136ff3f563f7ad5c3fd52552d \
        -X POST $deploy_job_url > $random_folder/deploy_job.txt
    deploy_job_result=`cat $random_folder/deploy_job.txt | python -c "import sys, json; print json.load(sys.stdin)['result']"`
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

#rm -rf $random_folder

if [ "$deploy_job_result" == "SUCCESS" ]; then
   exit 0;
else
   echo "failure / gave up"
   exit 1;
fi
