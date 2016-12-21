echo " "
echo "Start - Setting Publish Environment"
echo " "

echo "Setting Environment"

cd /opt/adobe/publish

. ./setenv.sh 

echo $CQ_JVM_OPTS
echo $CQ_RUNMODE
echo $CQ_PORT

echo "Starting the Instance"

cd /opt/adobe/publish/crx-quickstart/bin

./start

sleep 1m

STARTED=0

while [ $STARTED -eq 0 ]; do
	RESULT=$(curl -u admin:admin http://ld4443.homedepot.com:4503/welcome.html)
	CURL_STATUS=$?  	
	echo "Curl Status  ==> $CURL_STATUS"
        echo "Result Value ==> ${#RESULT}"
        if [ $CURL_STATUS -eq 7 ] || [ ${#RESULT} -gt 0 ]; then 
		echo "Instance is starting ..."
		STARTED=0
	else
		STARTED=1
		echo "Instance started."
	fi
	
	sleep 10s
done