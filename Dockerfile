FROM maven:3.6.3-jdk-11 AS build  
COPY . /ecosystem-user-service
RUN mvn -f /ecosystem-user-service/pom.xml clean package
RUN ls 
RUN ls ./ecosystem-user-service/target


FROM adoptopenjdk/openjdk11:jre-11.0.10_9-alpine
WORKDIR /etc/careydevelopment

COPY ./ecosystem.properties .
COPY ./product.properties .
COPY ./server.p12 .

COPY --from=build ./ecosystem-user-service/target/ecosystem-user-service.jar .

EXPOSE 32010

ENTRYPOINT ["java", "-jar", "./ecosystem-user-service.jar"]
