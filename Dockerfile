FROM openjdk:8-jre-alpine

LABEL maintainer "Antoine Aumjaud <antoine_dev@aumjaud.fr>"

VOLUME /home/app/conf
VOLUME /var/run/docker.sock
EXPOSE 9080

WORKDIR /home/app
COPY build/libs/*.jar executablejar.jar

CMD java -cp .:conf -jar executablejar.jar

# docker run --name api-docker -p 10002:9080 -it -v /var/run/docker.sock:/var/run/docker.sock antoineaumjaud/api-docker:latest bash

