export http_proxy=http://str-www-proxy2-qa.homedepot.com:8080
export https_proxy=http://str-www-proxy2-qa.homedepot.com:8080

if [ $# -lt 1 ]; then
  echo 1>&2 "$0 epic_name"
  exit 2
fi

export epic_name=$1

export JENKINS_SERVER=104.198.103.152
export CRUMB=$(curl --user admin:76a4a60136ff3f563f7ad5c3fd52552d http://$JENKINS_SERVER/crumbIssuer/api/xml?xpath=concat\(//crumbRequestField,%22:%22,//crumb\))
curl -H "$CRUMB" \
     --user admin:76a4a60136ff3f563f7ad5c3fd52552d \
     -X POST http://$JENKINS_SERVER/job/create-epic-environment/buildWithParameters?delay=0\&epic_name=$epic_name
