FROM eclipse-temurin:17

COPY ./target/ecosystem-user-service.jar /
COPY ./carey-development-service-config.json /

ENV GOOGLE_APPLICATION_CREDENTIALS="/carey-development-service-config.json"

EXPOSE 32010
ENTRYPOINT ["java", "-jar", "./ecosystem-user-service.jar"]
