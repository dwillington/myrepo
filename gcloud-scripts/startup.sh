#! /bin/bash
sed -i -e 's/PermitRootLogin no/PermitRootLogin yes/g' /etc/ssh/sshd_config
mkdir /root/.ssh
echo "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDLTb3H2EukeP1kN2AtpWS/C1/9fsFOcpYm9CIpUIkAbXvPjNZMMqnd8KOsmWZ0nGBWsmpUGYCqhfZsqC0bw1RtR0OqWLmL0KayVInFQDheqJTEmM5dVHycgKxkOx+EQP4jcaSJehzzSf9IQpax07xhnuy7bX+A8B4SF4HRhzWGCb1ncTKwgtt2Mhw4mR9YPaFWQRMas97/9X4B1rVN0MHG1r1qhRXB36HfkyEtaCy386hHhFAOMDIQhgyNBXSzeB94+g2Cv6MMsBB1Nk5WQuO9te+Q4LqN7Lg1n0alm5+HGZIRveouRLpSNi10a+WzHc6b423sSgtFml7YxXtkwZcT root@instance-1" >> /root/.ssh/authorized_keys
service sshd restart
sudo mv /etc/localtime /etc/localtime.bak
sudo ln -s /usr/share/zoneinfo/America/New_York /etc/localtime
