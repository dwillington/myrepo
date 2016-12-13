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

cd /opt/adobe/publish

echo " "
echo "Upload Packages"
echo " "

curl -u admin:admin -F package=@"AEM 6.0 Service Pack 2-1.0.zip" http://ld4443.homedepot.com:4503/crx/packmgr/service/.json?cmd=upload

curl -u admin:admin -F package=@"cq-6.0.0-hotfix-6167-bundles-1.4.zip" http://ld4443.homedepot.com:4503/crx/packmgr/service/.json?cmd=upload

curl -u admin:admin -F package=@"cq-hybris-content-6.0.0.zip" http://ld4443.homedepot.com:4503/crx/packmgr/service/.json?cmd=upload

curl -u admin:admin -F package=@"junit.zip" http://ld4443.homedepot.com:4503/crx/packmgr/service/.json?cmd=upload

curl -u admin:admin -F package=@"cq-6.0.0-hotfix-6446-1.0.zip" http://ld4443.homedepot.com:4503/crx/packmgr/service/.json?cmd=upload

curl -u admin:admin -F package=@"cq-6.0.0-featurepack-4137-1.8.zip" http://ld4443.homedepot.com:4503/crx/packmgr/service/.json?cmd=upload

curl -u admin:admin -F package=@"cq-6.0.0-hotfix-7310-1.0.2.zip" http://ld4443.homedepot.com:4503/crx/packmgr/service/.json?cmd=upload

curl -u admin:admin -F package=@"cq-6.0.0-hotfix-9456-1.0.zip" http://ld4443.homedepot.com:4503/crx/packmgr/service/.json?cmd=upload

echo " "
echo "Package Upload Completed"
echo " "

echo " "
echo "Install Packages"
echo " "

exit

RESULT=$(curl -u admin:admin -X POST http://ld4443.homedepot.com:4503/crx/packmgr/service/.json/etc/packages/adobe/cq600/servicepack/AEM%206.0%20Service%20Pack%202-1.0.zip?cmd=install)

echo $RESULT

RESULT=$(curl -u admin:admin -X POST http://ld4443.homedepot.com:4503/crx/packmgr/service/.json/etc/packages/adobe/cq600/hotfix/cq-6.0.0-hotfix-6167-bundles-1.4.zip?cmd=install)

echo $RESULT


count=0
res=1
until [ $res -eq 0 ]
do
    curl -u admin:admin http://ld4443.homedepot.com:4503/etc/packages/adobe/cq600/hotfix/cq-6.0.0-hotfix-6167-bundles-1.4.zip/jcr:content/vlt:definition/lastUnpacked.json 2>/dev/null | head -1 | grep -q "HTTP/1.1 200"
    res=$?
    if [ $((count++ % 10)) -eq 0 ]
     then
     echo “Waiting for service pack installation to finish…”
    fi
    sleep 1
done
sleep 300
echo “Service pack installation complete. You can restart the instance now.”

cd /opt/adobe/publish/crx-quickstart/bin

./stop

STR1="cq-quickstart-6.0.0-standalone-quickstart.jar"
echo $STR1
	
STOPPED=0

while [ $STOPPED -eq 0 ]; do

	RESULT=$(ps aux | grep "$STR1" | grep -v grep)
	FOUND1=$?
	echo $FOUND1
	
	if [ $FOUND1 -eq 0 ]; then
		echo "Instance is stopping ..."
	else
		echo "Instance is stopped"
		STOPPED=1
	fi

	sleep 10s
done

sleep 1m

./start

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

cd /opt/adobe/publish


RESULT=$(curl -u admin:admin -X POST http://ld4443.homedepot.com:4503/crx/packmgr/service/.json/etc/packages/A.O.%20Smith%20corporation/cq-hybris-content-6.0.0.zip?cmd=install)

echo $RESULT

RESULT=$(curl -u admin:admin -X POST http://ld4443.homedepot.com:4503/crx/packmgr/service/.json/etc/packages/my_packages/junit.zip?cmd=install)

echo $RESULT

RESULT=$(curl -u admin:admin -X POST http://ld4443.homedepot.com:4503/crx/packmgr/service/.json/etc/packages/day/cq600/hotfix/cq-6.0.0-hotfix-6446-1.0.zip?cmd=install)

echo $RESULT

RESULT=$curl -u admin:admin -X POST http://ld4443.homedepot.com:4503/crx/packmgr/service/.json/etc/packages/day/cq600/featurepack/cq-6.0.0-featurepack-4137-1.8.zip?cmd=install)

echo $RESULT

RESULT=$curl -u admin:admin -X POST http://ld4443.homedepot.com:4503/crx/packmgr/service/.json/etc/packages/adobe/cq600/hotfix/cq-6.0.0-hotfix-7310-1.0.2.zip?cmd=install)

echo $RESULT

RESULT=$curl -u admin:admin -X POST http://ld4443.homedepot.com:4503/crx/packmgr/service/.json/etc/packages/hotfix/cq-6.0.0-hotfix-9456-1.0.zip?cmd=install)

echo $RESULT

