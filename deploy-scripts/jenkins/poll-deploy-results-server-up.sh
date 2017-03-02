#/bin/sh
export http_proxy=http://str-www-proxy2-qa.homedepot.com:8080
export https_proxy=http://str-www-proxy2-qa.homedepot.com:8080

if [ $# -lt 2 ]; then
  echo 1>&2 "$0: provide epic_name and project_name"
  exit 2
fi

export epic_name=$1
export project_name=$2

export SLEEP_PERIOD=2
export server_url=`gcloud-scripts/epic-server-urls.sh $epic_name $project_name`

# export http_code=000

# we may wish to change this to ! -eq 200
# while [ "$x" -lt 30 -a ! "$http_code" -eq "000" ]; do
    # export http_code=`curl --max-time 5 -s -o /dev/null -I -w "%{http_code}" $server_url`
    # x=$((x+1))
    # sleep $SLEEP_PERIOD
# done

while true
do
  http_code=`curl --max-time 5 -s -o /dev/null -I -w "%{http_code}" $server_url`
  x=$((x+1))
  if [ $http_code -eq 200 ]; then
    echo "Got 200! All done!"
    break
  elif [ "$x" -gt 30 ]; then
    echo "Giving up!"
    break
  else
    echo "Got $STATUS :( Not done yet..."
  fi
  sleep $SLEEP_PERIOD
done

if [ "$http_code" -eq "200" ]; then
   exit 0;
else
   exit 1;
fi