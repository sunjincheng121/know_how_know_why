FROM centos:7
MAINTAINER jincheng.sunjc@gmail.com
LABEL author=khkw

RUN yum -y install httpd &&  mkdir -p /var/www/html && echo "Apache Flink Dockerfile demo " >/var/www/html/index.html
EXPOSE 80

CMD ["httpd","-DFOREGROUND"]

