package com.epam.resourceprocessor.contract;

import com.epam.resourceprocessor.ResourceProcessorApplication;
import com.epam.resourceprocessor.consumer.ResourceConsumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.stubrunner.StubTrigger;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;


@SpringBootTest(classes = ResourceProcessorApplication.class)
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:resource-processor-test.properties")
@AutoConfigureStubRunner(ids = "com.epam:resource-service", stubsMode = StubRunnerProperties.StubsMode.LOCAL)
public class ResourceProcessorContractTestBaseClass {
    @Autowired
    private ResourceConsumer resourceConsumer;
    
    @Autowired
    StubTrigger stubTrigger;

    @MockBean
    RestTemplate restTemplate;

    @Before
    public void init() {
        byte[] data = new byte[]{1, 2, 3};

        when(restTemplate.getForObject(any(), any(), eq(1L))).thenReturn(data);
    }

    @Test
    public void testMessageConsumer() {
        stubTrigger.trigger("song.created.event");
    }
}
