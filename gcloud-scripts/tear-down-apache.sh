export SSH_ARGS='-o StrictHostKeyChecking=no'
export DESTINATION_HOST=$1

ssh $SSH_ARGS root@$DESTINATION_HOST "docker ps -a"
ssh $SSH_ARGS root@$DESTINATION_HOST "docker stop \$(docker ps -a -q)"
ssh $SSH_ARGS root@$DESTINATION_HOST "docker rm \$(docker ps -a -q)"
ssh $SSH_ARGS root@$DESTINATION_HOST "docker ps -a"
