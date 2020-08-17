FROM centos:centos7
RUN yum install epel-release -y
RUN yum install ansible -y
RUN yum install git -y
RUN yum install initscripts -y 
RUN yum install firewalld -y 
RUN yum install wget -y 
RUN (cd /lib/systemd/system/sysinit.target.wants/; for i in *; do [ $i == systemd-tmpfiles-setup.service ] || rm -f $i; done); \
rm -f /lib/systemd/system/multi-user.target.wants/*;\
rm -f /etc/systemd/system/*.wants/*;\
rm -f /lib/systemd/system/local-fs.target.wants/*; \
rm -f /lib/systemd/system/sockets.target.wants/*udev*; \
rm -f /lib/systemd/system/sockets.target.wants/*initctl*; \
rm -f /lib/systemd/system/basic.target.wants/*;\
rm -f /lib/systemd/system/anaconda.target.wants/*;
CMD /usr/sbin/init
######################################################################
RUN yum install openssh openssh-server openssh-clients openssl-libs sudo -y
#RUN sed -i 's/#Port 22/Port 2022/' /etc/ssh/sshd_config
RUN yum install java-1.8.0-openjdk unzip -y
RUN useradd aashraf
RUN usermod -aG wheel aashraf # add to sudo
RUN groupadd jbs1appg
RUN useradd -s /bin/bash -g jbs1appg jbs1app
RUN mkdir -p /opt/jboss /opt/Canada /opt/deploy /opt/tuc-prod
COPY jboss-eap-6.4.tgz /tmp/jboss-eap-6.4.tgz
RUN tar -xzvf /tmp/jboss-eap-6.4.tgz -C /opt/jboss
RUN chown -Rf jbs1app:jbs1appg /opt/jboss /opt/Canada /opt/deploy /opt/tuc-prod

