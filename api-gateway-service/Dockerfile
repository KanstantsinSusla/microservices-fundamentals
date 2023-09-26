FROM amazoncorretto:11
ARG JAR_FILE=target/*.jar
COPY ./target/api-gateway-service-0.0.1-SNAPSHOT.jar api-gateway-service.jar
ENTRYPOINT ["java", "-jar", "/api-gateway-service.jar"]
