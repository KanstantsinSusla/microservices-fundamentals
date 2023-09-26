FROM amazoncorretto:11
ARG JAR_FILE=target/*.jar
COPY ./target/authorization-server-0.0.1-SNAPSHOT.jar authorization-server.jar
ENTRYPOINT ["java", "-jar", "/authorization-server.jar"]
