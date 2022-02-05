FROM centos:7
MAINTAINER jincheng.sunjc@gmail.com

RUN yum -y install httpd &&  mkdir -p /var/www/html

COPY ./web/index.html /var/www/html/
CMD ["httpd","-DFOREGROUND"]

