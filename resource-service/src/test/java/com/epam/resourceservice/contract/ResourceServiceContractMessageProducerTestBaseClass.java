package com.epam.resourceservice.contract;

import com.epam.resourceservice.ResourceServiceApplication;
import com.epam.resourceservice.controller.ResourceController;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ResourceServiceApplication.class)
@AutoConfigureMessageVerifier
@TestPropertySource(locations = "classpath:application-integration-test.properties")
public abstract class ResourceServiceContractMessageProducerTestBaseClass {
    @Autowired
    public ResourceController resourceController;


    public void sendSongIdMessage() {
        System.out.println("Invoking method for sending message.");

        Map<String, Long> messageBody = Collections.singletonMap("id", 1L);

        resourceController.sendMessage(messageBody);
    }
}
