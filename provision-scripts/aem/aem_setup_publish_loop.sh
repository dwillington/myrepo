cd /opt/adobe/publish

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