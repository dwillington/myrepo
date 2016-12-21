echo "                           "
echo "Switching to Root Directory"
echo "                           "

cd /root

yum install docker -y

echo "HTTP_PROXY=http://str-www-proxy2-qa.homedepot.com:8080" >> /etc/sysconfig/docker
echo "HTTPS_PROXY=http://str-www-proxy2-qa.homedepot.com:8080" >> /etc/sysconfig/docker

systemctl start docker

#service docker restart

docker pull centos:centos6

docker run -d -p 80:80 -i -t --name apache centos:centos6 

docker ps -a

docker cp *.gz apache:/tmp/.
docker cp apache_setup_on_docker.sh apache:~/tmp/.

#docker exec -i -t apache /bin/bash 

echo "                           "
echo "Process Completed"
echo "                           "
