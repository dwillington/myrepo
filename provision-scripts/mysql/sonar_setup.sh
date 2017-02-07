yum install java wget unzip -y
wget https://sonarsource.bintray.com/Distribution/sonarqube/sonarqube-6.2.zip
unzip sonarqube-6.2.zip
mv sonarqube-6.2 /opt/.
sed -i "110i sonar.web.port=80" /opt/sonarqube-6.2/conf/sonar.properties
grep -in sonar.web.port /opt/sonarqube-6.2/conf/sonar.properties
/opt/sonarqube-6.2/bin/linux-x86-64/sonar.sh start

tail -100f /opt/sonarqube-6.2/logs/sonar.log 
