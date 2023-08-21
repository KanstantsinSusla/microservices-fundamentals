package com.epam.resourceservice.integration;

import com.epam.resourceservice.service.AmazonS3Service;
import com.epam.resourceservice.service.ResourceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-integration-test.properties")
public class ResourceServiceIntegrationTest {
    @MockBean
    private AmazonS3Service amazonS3Service;

    @Autowired
    private ResourceService resourceService;

    @Test
    public void addResourceTest() {
        String bucketName = "songs";
        byte[] data = new byte[]{};
        String resourceKey = "1";

        when(amazonS3Service.addResource(any(), any())).thenReturn(bucketName);

        Long resourceId = resourceService.addResource(data, resourceKey);

        assertNotNull(resourceId);
    }

    @Test
    public void getResourceByIdTest() throws IOException {
        byte[] data = new byte[]{1,2,3};

        when(amazonS3Service.getResource("songs", "1")).thenReturn(data);

        byte[] resourceById = resourceService.getResourceById(2L);

        assertNotNull(resourceById);
    }
}
