#!/bin/sh

if [ $# lt 1 ]; then
    echo "create-tcserver.sh port"
    exit 1
fi

port=$1
cid=$(docker ps -a | grep $port | sed 's/|/ /' | awk '{print $1}')

if [ ! -z "$cid" ]
then
  echo "$cid tcserver already running on $port"
else
  echo "creating tcserver running on $port"
  cid=$(docker run -dit -p $port:8080 tcserver-centos)
fi