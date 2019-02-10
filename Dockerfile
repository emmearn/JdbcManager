FROM tomcat
MAINTAINER Marco Arnone, https://github.com/emmearn
WORKDIR /usr/local/tomcat/webapps/
RUN rm -R *
COPY ./web/target/jdbcmanager-web-0.0.4-SNAPSHOT.war .
RUN mv jdbcmanager-web-0.0.4-SNAPSHOT.war app.war
RUN ../bin/catalina.sh start
EXPOSE 8080