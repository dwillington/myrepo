echo " "
echo "Start - Setting Publish Environment"
echo " "

echo "Setting Environment"

cd /opt/adobe/publish

sed -i -e 's/4096/4096/g' setenv.sh

. ./setenv.sh 

echo $CQ_JVM_OPTS
echo $CQ_RUNMODE
echo $CQ_PORT

echo "Starting the Instance"

cd /opt/adobe/publish/crx-quickstart/bin

./start

cd /opt/adobe/publish

tail -100 /opt/adobe/publish/crx-quickstart/logs/stdout.log