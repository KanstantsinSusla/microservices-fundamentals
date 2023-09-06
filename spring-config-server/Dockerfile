FROM amazoncorretto:11
ARG JAR_FILE=target/*.jar
COPY ./target/spring-config-server-0.0.1-SNAPSHOT.jar spring-config-server.jar
ENTRYPOINT ["java", "-jar", "/spring-config-server.jar"]
