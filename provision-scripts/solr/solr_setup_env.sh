echo "                "
echo "Extracting Java File"
echo "                "

cd /opt/solr

#done inside solar.tar
#tar -xvf jdk1.7.0_75.tar

echo "               "
echo "Start - Setting Java Base"
echo "               "

update-alternatives --install "/usr/bin/java" "java" "/opt/solr/jdk1.7.0_75/bin/java" 1
update-alternatives --install "/usr/bin/javac" "javac" "/opt/solr/jdk1.7.0_75/bin/javac" 1
update-alternatives --install "/usr/bin/javaws" "javaws" "/opt/solr/jdk1.7.0_75/bin/javaws" 1

echo "               "
echo "End - Setting Java Base"
echo "               "

echo " "
echo "Start - Extract file"
echo " "

tar -xvf ~/solr.tar

echo " "
echo "End - Extract file"
echo " "

# TODO
# change to apache-tomcat-7.0.68
# cd /opt/solr/solr-home/homedepot 
# rm -rf ??? so we have a clean install

ln -s apache-tomcat-7.0.57 tomcat