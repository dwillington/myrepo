#/bin/sh
export http_proxy=http://str-www-proxy2-qa.homedepot.com:8080
export https_proxy=http://str-www-proxy2-qa.homedepot.com:8080

if [ $# -lt 2 ]; then
  echo 1>&2 "$0: provide epic_name and project_name"
  exit 2
fi

export epic_name=$1
export project_name=$2

export server_url=`gcloud-scripts/epic-server-urls.sh $epic_name $project_name`
export http_code=`curl -s -o /dev/null -I -w "%{http_code}" $server_url`

if [ "$http_code" -eq "200" ]; then
   exit 0;
else
   echo "http_code: $http_code";
   exit 1;
fi