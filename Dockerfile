FROM openjdk:17-jdk-alpine
VOLUME /tmp
COPY target/email-verifier-rest-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]