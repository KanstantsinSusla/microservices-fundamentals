FROM amazoncorretto:11
ARG JAR_FILE=target/*.jar
COPY ./target/storage-service-0.0.1-SNAPSHOT.jar storage-service.jar
ENTRYPOINT ["java", "-jar", "/storage-service.jar"]
