echo "                "
echo "Extracting Java File"
echo "                "

cd /opt/adobe/publish

echo " "
echo "Start - Extract Jar file"
echo " "

tar -cvf aem.jar.gz

echo " "
echo "End - Extractjar file"
echo " "

echo "               "
echo "Start - Setting Java Base"
echo "               "

update-alternatives --install "/usr/bin/java" "java" "/opt/adobe/publish/jdk1.7.0_75/bin/java" 1
update-alternatives --install "/usr/bin/javac" "javac" "/opt/adobe/publish/jdk1.7.0_75/bin/javac" 1
update-alternatives --install "/usr/bin/javaws" "javaws" "/opt/adobe/publish/jdk1.7.0_75/bin/javaws" 1

echo "               "
echo "End - Setting Java Base"
echo "               "
