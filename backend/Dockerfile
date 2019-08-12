FROM java:openjdk-8-jdk
MAINTAINER Jan Wiegmann <jw@jwiegmann.de>

ARG JAR_FILE
ADD target/${JAR_FILE} /opt/twttr-api.jar

ENV JAVA_OPTS="-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true"

EXPOSE 8081
ENTRYPOINT exec java $JAVA_OPTS -jar /opt/twttr-api.jar
