echo "                           "
echo "Switching to Root Directory"
echo "                           "

cd /root

sudo yum install docker -y

echo "HTTP_PROXY=http://str-www-proxy2-qa.homedepot.com:8080" >> /etc/sysconfig/docker
echo "HTTPS_PROXY=http://str-www-proxy2-qa.homedepot.com:8080" >> /etc/sysconfig/docker

sudo systemctl start docker

#service docker restart

sudo docker pull centos:centos6

docker run -d -p 80:80 -i -t --name apache centos:centos6 

docker ps -a

docker cp *.gz apache:~/.
#docker cp *.gz apache:~/apache_setup_on_docker.sh

#sudo docker exec -i -t apache /bin/bash 

echo "                           "
echo "Process Completed"
echo "                           "
