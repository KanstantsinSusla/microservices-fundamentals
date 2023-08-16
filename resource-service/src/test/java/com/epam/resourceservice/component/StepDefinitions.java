package com.epam.resourceservice.component;

import com.amazonaws.services.s3.AmazonS3;
import com.epam.resourceservice.model.AddResourceResponse;
import com.epam.resourceservice.service.ResourceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.junit.Assert.*;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integration-test.properties")
public class StepDefinitions {
    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private ResourceService resourceService;

    @LocalServerPort
    private int port;

    private ResponseEntity<String> response;
    private AddResourceResponse addResourceResponse;

    @When("the client calls {string}")
    public void the_client_calls_resources(String endpoint) {
        try {
            HttpEntity<byte[]> data = new HttpEntity<>(new byte[]{1, 2, 3});

            response = new RestTemplate().exchange("http://localhost:" + port + endpoint, HttpMethod.POST,  data, String.class);
            String responseBody = response.getBody();

            assertNotNull(responseBody);

            addResourceResponse = new ObjectMapper().readValue(responseBody, AddResourceResponse.class);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Then("the client receives status code of {int}")
    public void the_client_receives_status_code_of(int statusCode) {
        assertEquals(statusCode, response.getStatusCodeValue());
    }

    @Then("clean resources")
    public void clean_resources() {
        resourceService.deleteResourcesByIds(Collections.singletonList(addResourceResponse.getId()));

        boolean flag = true;

        while (flag) {
            Message message = template.receive("resource.queue");

            if (message == null) {
                flag = false;
            }
        }
    }
}
