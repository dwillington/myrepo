export http_proxy=http://str-www-proxy2-qa.homedepot.com:8080
export https_proxy=http://str-www-proxy2-qa.homedepot.com:8080

if [ $# -lt 2 ]; then
  echo 1>&2 "$0: must provide epic_name and project_name"
  exit 2
fi

export epic_name=epic2
export project_name=solr
export json=\''{"parameter": [{"name":"epic_name", "value":"'$epic_name'"}, {"name":"project_name", "value":"'$project_name'"}]}'\'
echo $json

export JENKINS_SERVER=104.198.103.152
export CRUMB=$(curl --user admin:76a4a60136ff3f563f7ad5c3fd52552d http://$JENKINS_SERVER/crumbIssuer/api/xml?xpath=concat\(//crumbRequestField,%22:%22,//crumb\))
curl -X POST http://$JENKINS_SERVER/job/deploy-epic-project/build \
     -H "$CRUMB" \
     --user admin:76a4a60136ff3f563f7ad5c3fd52552d \
     --data-urlencode json=$json
