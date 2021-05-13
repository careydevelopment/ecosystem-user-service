FROM adoptopenjdk/openjdk11:jre-11.0.10_9-alpine
COPY ./target/ecosystem-user-service.jar /
COPY ../../../../../ecosystem.properties /etc/careydevelopment
EXPOSE 32010
ENTRYPOINT ["java", "-jar", "./ecosystem-user-service.jar"]
