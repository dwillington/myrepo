username=admin
password=1196942d9836374d0f56331fcf24480c01
CRUMB=$(curl -u $username:$password -s 'http://localhost:8081/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,":",//crumb)')
curl -u $username:$password -H "$CRUMB" -s -XPOST 'http://localhost:8081/createItem?name=a-seed-job' --data-binary @config.xml -H "Content-Type:text/xml"