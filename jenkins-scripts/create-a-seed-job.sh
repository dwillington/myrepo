username=admin
password=119bae1e944363287dae0b69ada2fedee7
HOST=35.236.201.124:8080
#CRUMB=$(curl -u $username:$password -s 'http://$HOST/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,":",//crumb)')
CRUMB=$(curl --user $username:$password http://$HOST/crumbIssuer/api/xml?xpath=concat\(//crumbRequestField,%22:%22,//crumb\))
curl --user $username:$password -H "$CRUMB" -s -XPOST http://$HOST/createItem?name=a-seed-job --data-binary @config.xml -H "Content-Type:text/xml"