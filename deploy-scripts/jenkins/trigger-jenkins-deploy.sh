export http_proxy=http://str-www-proxy2-qa.homedepot.com:8080
export https_proxy=http://str-www-proxy2-qa.homedepot.com:8080

if [ $# -lt 2 ]; then
  echo 1>&2 "$0: must provide epic_name and project_name"
  exit 2
fi

export epic_name=$1
export project_name=$2

export random_folder=delete-me-$RANDOM
mkdir -p /tmp/$random_folder

export JENKINS_SERVER=104.198.103.152
export CRUMB=$(curl --user admin:76a4a60136ff3f563f7ad5c3fd52552d http://$JENKINS_SERVER/crumbIssuer/api/xml?xpath=concat\(//crumbRequestField,%22:%22,//crumb\))
curl -i --netrc -H "$CRUMB" \
     --user admin:76a4a60136ff3f563f7ad5c3fd52552d \
     -X POST http://$JENKINS_SERVER/job/deploy-epic-project/buildWithParameters?delay=0\&epic_name=$epic_name\&project_name=$project_name | tee /tmp/$random_folder/out.txt.orig

poll-deploy-job-results.sh $random_folder

# the following will not work because $epic_name/$project_name are in '' so they will be taken literally and not substitued
#curl -X POST http://$JENKINS_SERVER/job/deploy-epic-project/build \
#     -H "$CRUMB" \
#     --user admin:76a4a60136ff3f563f7ad5c3fd52552d \
#     --data-urlencode json='{"parameter": [{"name":"epic_name", "value":"$epic_name"}, {"name":"project_name", "value":"$project_name"}]}'

