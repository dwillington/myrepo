#/bin/sh
SLEEP_PERIOD=2
NUM_ATTEMPTS=30

x=0
while true
do
    result=$(/etc/init.d/solr status | grep "No Solr nodes are running")
    if [ "$result" == "No Solr nodes are running." ]; then
       echo "result=$result"
       break
    fi
    if [ "$x" -gt "$NUM_ATTEMPTS" ]; then
      echo "killing forecefully"
      kill $(ps aux | grep '[j]ava' | awk '{print $2}')
      break
    fi
    sleep $SLEEP_PERIOD
    x=$((x+1))
done
