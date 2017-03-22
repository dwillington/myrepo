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
if [ $# -gt 2 ]; then
    export NUM_ATTEMPTS=$3
fi

if [[ $(hostname) = ln0a7b.* ]]; then
  server_url=`/bamboo/data/thdutil/serversetup/myrepo/gcloud-scripts/epic-server-urls.sh $epic_name $project_name`
#elif [[ $(hostname) = ld5717* ]]; then
else
  server_url=`gcloud-scripts/epic-server-urls.sh $epic_name $project_name`
fi

# https://coderwall.com/p/taqiyg/use-http-status-codes-from-curl
x=0
while true
do
  http_code=`curl --max-time 5 -s -o /dev/null -I -w "%{http_code}" $server_url`
  if [ "$http_code" == "200" ] || [ "$project_name" == "hybris" && "$http_code" == "302" ]; then
    echo "$project_name got $http_code! All done!"
    break
  else
    echo "$project_name got $http_code :( Not done yet..."
  fi
  if [ "$x" -gt "$NUM_ATTEMPTS" ]; then
    echo "Giving up!"
    break
  fi
  sleep $SLEEP_PERIOD
  x=$((x+1))
done

  if [ "$http_code" == "200" ] || [ "$project_name" == "hybris" && "$http_code" == "302" ]; then
   exit 0;
else
   exit 1;
fi