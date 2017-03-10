export http_proxy=http://str-www-proxy2-qa.homedepot.com:8080
export https_proxy=http://str-www-proxy2-qa.homedepot.com:8080

if [ $# -lt 2 ]; then
  echo 1>&2 "$0: job_name epic_name [project_name]"
  exit 2
fi

export job_name=$1
export epic_name=$2
export project_name=$3

random_folder=/tmp/delete-me-$job_name-$epic_name-$project_name
mkdir -p $random_folder

export JENKINS_SERVER=104.198.103.152
export CRUMB=$(curl --user admin:76a4a60136ff3f563f7ad5c3fd52552d http://$JENKINS_SERVER/crumbIssuer/api/xml?xpath=concat\(//crumbRequestField,%22:%22,//crumb\))
curl -i --netrc -H "$CRUMB" \
     --user admin:76a4a60136ff3f563f7ad5c3fd52552d \
     -X POST http://$JENKINS_SERVER/job/$job_name/buildWithParameters?delay=0\&epic_name=$epic_name\&project_name=$project_name | tee $random_folder/out.txt.orig