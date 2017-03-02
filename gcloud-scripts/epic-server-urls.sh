#!/bin/bash

if [ $# -lt 1 ]; then
  echo 1>&2 "$0: missing epic_name"
  exit 2
fi

export epic_name=$1
#echo "epic_name is set to '$epic_name'";

gcloud compute instances list --regexp=$epic_name.* > out.txt
sed -ie '1d' out.txt # remove line 1
awk '{print $9}' out.txt > out1.txt # only keep column 9, which is the ip address
mv -f out1.txt out.txt
sed -i -e 's/^/http:\/\//' out.txt
sed -i '1s/$/\/libs\/granite\/core\/content\/login.html/' out.txt
sed -i '2s/$/\/en\/home.html/' out.txt
sed -i '3s/$/\/hmc\/hybris/' out.txt
sed -i '4s/$/\/phpmyadmin\//' out.txt
sed -i '5s/$/\/solr\//' out.txt

if [ $# -lt 2 ]
then
  cat out.txt
else
  export project_name=$2
  case $project_name in
    "aem" )
        sed -n 1p out.txt;;
    "apache" )
        sed -n 2p out.txt;;
    "hybris" )
        sed -n 3p out.txt;;
    "mysql" )
        sed -n 4p out.txt;;
    "solr" )
        sed -n 5p out.txt;;
  esac
fi
