package com.epam.apigatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class APIGatewayServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(APIGatewayServiceApplication.class, args);
    }
}
