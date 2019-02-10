FROM ubuntu:18.04
MAINTAINER Marco Arnone, https://github.com/emmearn
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y openjdk-8-jdk && \
    apt-get install -y tomcat8 && \
    apt-get clean

EXPOSE 8080
#/usr/share/tomcat8/bin/catalina.sh