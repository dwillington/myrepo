echo "                "
echo "Extracting Java File"
echo "                "

cd /opt/adobe/publish

tar -xvf jdk1.7.0_75.tar

echo "               "
echo "Start - Setting Java Base"
echo "               "

update-alternatives --install "/usr/bin/java" "java" "/opt/adobe/publish/jdk1.7.0_75/bin/java" 1
update-alternatives --install "/usr/bin/javac" "javac" "/opt/adobe/publish/jdk1.7.0_75/bin/javac" 1
update-alternatives --install "/usr/bin/javaws" "javaws" "/opt/adobe/publish/jdk1.7.0_75/bin/javaws" 1

echo "               "
echo "End - Setting Java Base"
echo "               "

echo " "
echo "Start - Extract Jar file"
echo " "

java -jar cq6-publish-p4503.jar -unpack

echo " "
echo "End - Extractjar file"
echo " "
