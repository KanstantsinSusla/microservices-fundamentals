FROM amazoncorretto:11
ARG JAR_FILE=target/*.jar
COPY ./target/discovery-service-0.0.1-SNAPSHOT.jar discovery-service.jar
ENTRYPOINT ["java", "-jar", "/discovery-service.jar"]