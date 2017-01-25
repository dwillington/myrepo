docker cp *.gz apache:/tmp/.
docker cp apache_deploy_on_docker.sh apache:/tmp/.
docker exec apache /bin/sh -c /tmp/apache_deploy_on_docker.sh
