FROM adoptopenjdk/openjdk11:jre-11.0.10_9-alpine
RUN ls
RUN mv ./ecosystem-user-service.jar .
EXPOSE 32010
ENTRYPOINT ["java", "-jar", "./ecosystem-user-service.jar"]
