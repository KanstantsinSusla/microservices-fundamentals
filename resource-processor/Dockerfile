FROM amazoncorretto:11
ARG JAR_FILE=target/*.jar
COPY ./target/resource-processor-0.0.1-SNAPSHOT.jar resource-processor.jar
ENTRYPOINT ["java", "-jar", "/resource-processor.jar"]
