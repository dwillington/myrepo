export DESTINATION_HOST=$1

scp unprovision-node.sh root@$DESTINATION_HOST:~/.

ssh root@$DESTINATION_HOST chmod u+x /root/*.sh

ssh root@$DESTINATION_HOST /root/unprovision-node.sh
