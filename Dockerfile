FROM adoptopenjdk/openjdk11:jre-11.0.10_9-alpine

WORKDIR /etc/careydevelopment

COPY ./ecosystem.properties .
COPY ./server.p12 .

WORKDIR /ecosystem-user-service

COPY /target/ecosystem-user-service.jar .

EXPOSE 32010

ENTRYPOINT ["java", "-jar", "./ecosystem-user-service.jar"]
