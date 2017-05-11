FROM openjdk:8-jre-alpine

RUN apk --no-cache add docker

LABEL maintainer "Antoine Aumjaud <antoine_dev@aumjaud.fr>"

VOLUME /var/run/docker.sock
EXPOSE 9080

WORKDIR /home/app
ADD build/distributions/api-docker.tar .
VOLUME ./api-docker/lib/conf

RUN mkdir ./logs 
VOLUME ./logs

CMD    ./api-docker/bin/api-docker


