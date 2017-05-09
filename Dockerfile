FROM openjdk:8-jre-alpine

LABEL maintainer "Antoine Aumjaud <antoine_dev@aumjaud.fr>"

VOLUME /var/run/docker.sock
EXPOSE 9080

WORKDIR /home/app
ADD build/distributions/api-docker.tar .

VOLUME ./api-docker/lib/conf
CMD    ./api-docker/bin/api-docker

# docker run --name api-docker -p 10002:9080 -it -v /var/run/docker.sock:/var/run/docker.sock antoineaumjaud/api-docker:latest bash

