echo " "
echo "Connected to the Server"
echo " "

echo " "
echo "Switch to Base Directory"
echo " "

cd /

echo " "
echo "Create opt Directory"
echo " "

mkdir opt

echo " "
echo "Switch to the opt Directory"
echo " "

cd opt

echo " "
echo "Create Directory"
echo " "

mkdir jdk
mkdir hybris

echo " "
echo "Switch to the Directory"
echo " "

cd hybris
mkdir security

yum install unzip -y

# create service entry to stop/start hybris