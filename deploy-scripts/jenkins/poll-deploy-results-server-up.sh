#/bin/sh
export http_proxy=http://str-www-proxy2-qa.homedepot.com:8080
export https_proxy=http://str-www-proxy2-qa.homedepot.com:8080

if [ $# -lt 2 ]; then
  echo 1>&2 "$0: provide epic_name project_name [num_attempts]"
  exit 2
fi

export epic_name=$1
export project_name=$2

export SLEEP_PERIOD=5
export NUM_ATTEMPTS=5
if [ $# -gt 2 ]
then
    export NUM_ATTEMPTS=$3
else

export server_url=`gcloud-scripts/epic-server-urls.sh $epic_name $project_name`

#https://coderwall.com/p/taqiyg/use-http-status-codes-from-curl
while true
do
  http_code=`curl --max-time 5 -s -o /dev/null -I -w "%{http_code}" $server_url`
  x=$((x+1))
  if [ $http_code -eq 200 ]; then
    echo "Got 200! All done!"
    break
  elif [ "$x" -gt $NUM_ATTEMPTS ]; then
    echo "Giving up!"
    break
  else
    echo "Got $http_code :( Not done yet..."
  fi
  sleep $SLEEP_PERIOD
done

if [ "$http_code" -eq "200" ]; then
   exit 0;
else
   exit 1;
fi