FROM openjdk:8-jre-alpine

LABEL maintainer "Antoine Aumjaud <antoine_dev@aumjaud.fr>"

VOLUME /home/app/conf
VOLUME /var/run/docker.sock
EXPOSE 9080

WORKDIR /home/app
COPY build/libs/*.jar executablejar.jar

CMD java -cp .:conf -jar executablejar.jar