FROM openjdk:11-slim-buster

ARG BUILD_VERSION

WORKDIR /program

ADD bin/ bin/
COPY lib/ lib/

ENTRYPOINT [ "/bin/bash", "/program/bin/graph-traveler" ]
 
