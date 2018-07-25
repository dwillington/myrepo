username=admin
password=cb00504b4c43553105c812991723d874
CRUMB=$(curl -u $username:$password -s 'http://localhost:8081/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,":",//crumb)')
curl -u $username:$password -H "$CRUMB" -s -XPOST 'http://localhost:8081/createItem?name=a-seed-job' --data-binary @config.xml -H "Content-Type:text/xml"