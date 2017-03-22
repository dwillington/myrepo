yum -y install glibc.i686 install libstdc++-4.8.5-11.el7.i686 unzip -y

for SCRIPT in /root/mountebank-v1.8.0/services/*
do
    if [ -f $SCRIPT -a -x $SCRIPT ]
    then
        $SCRIPT
    fi
done
