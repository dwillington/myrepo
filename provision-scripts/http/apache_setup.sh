echo "                           "
echo "Switching to Root Directory"
echo "                           "

cd /root

echo "                           "
echo "Install Required Software"
echo "                           "
yum -y install mod_ssl openssl httpd curl

echo "                           "
echo "Creating Required Directories"
echo "                           "

cd /
mkdir opt
cd opt
mkdir apache


echo "                           "
echo "Process Completed"
echo "                           "
