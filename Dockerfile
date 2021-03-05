FROM maven:3.6.3-jdk-11 AS build  
COPY /var/lib/jenkins/workspace/ecosystem-user-service /ecosystem-user-service
RUN mvn -f /ecosystem-user-service/pom.xml clean package



FROM adoptopenjdk/openjdk11:jre-11.0.10_9-alpine

WORKDIR /etc/careydevelopment

COPY ./ecosystem.properties .
COPY ./server.p12 .

WORKDIR /ecosystem-user-service

COPY /target/ecosystem-user-service.jar .

EXPOSE 32010

ENTRYPOINT ["java", "-jar", "./ecosystem-user-service.jar"]
