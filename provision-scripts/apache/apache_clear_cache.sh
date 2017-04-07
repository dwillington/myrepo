cache_cmd1=`curl -X POST -H "CQ-Action: Delete" -H "CQ-Handle: /content/homedepot_ca" -H "Content-Length: 0" -H "Content-Type: application/octet-stream"  http://127.0.0.1/dispatcher/invalidate.cache`
echo $cache_cmd1
cache_cmd2=`curl -X POST -H "CQ-Action: Delete" -H "CQ-Handle: /etc/designs/homedepot" -H "Content-Length: 0" -H "Content-Type: application/octet-stream"  http://127.0.0.1/dispatcher/invalidate.cache`
echo $cache_cmd2
cache_cmd3=`curl -X POST -H "CQ-Action: Delete" -H "CQ-Handle: /is" -H "Content-Length: 0" -H "Content-Type: application/octet-stream"   http://127.0.0.1/dispatcher/invalidate.cache`
echo $cache_cmd3
cache_cmd4=`curl -X POST -H "CQ-Action: Delete" -H "CQ-Handle: /libs/cq/i18n" -H "Content-Length: 0" -H "Content-Type: application/octet-stream"  http://127.0.0.1/dispatcher/invalidate.cache`
echo $cache_cmd4
cache_cmd5=`curl -X POST -H "CQ-Action: Delete" -H "CQ-Handle: /content/dam" -H "Content-Length: 0" -H "Content-Type: application/octet-stream"   http://127.0.0.1/dispatcher/invalidate.cache`
echo $cache_cmd5