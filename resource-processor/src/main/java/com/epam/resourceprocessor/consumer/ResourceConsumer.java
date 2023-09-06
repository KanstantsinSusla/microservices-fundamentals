package com.epam.resourceprocessor.consumer;

import com.epam.resourceprocessor.converter.SongMetadataConverter;
import com.epam.resourceprocessor.model.ResourceLink;
import com.epam.resourceprocessor.model.SongRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.tika.exception.TikaException;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;

import java.io.IOException;

@Log4j2
@RestController
@RefreshScope
public class ResourceConsumer {
    private static final String RESOURCE_QUEUE = "resource.queue";

    private static final String RESOURCE_EXCHANGE = "resource_exchange";
    private static final String RESOURCE_EXCHANGE_TYPE = "topic";

    private static final String DLQ_EXCHANGE_ARG = "x-dead-letter-exchange";
    private static final String DLQ_ROUTING_KEY_ARG = "x-dead-letter-routing-key";

    private static final String RESOURCE_EXCHANGE_DLQ = "resource_exchange_dlq";

    private static final String RESOURCE_ROUTING_KEY = "resource_routing_key";
    private static final String RESOURCE_ROUTING_KEY_DLQ = "resource_routing_key_dlq";

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SongMetadataConverter songMetadataConverter;

    @Value("${resource.service.get.endpoint}")
    private String resourceServiceGetEndpoint;

    @Value("${song.service.endpoint}")
    private String songServiceEndpoint;

    @Value("${test.value:NO TEST VALUE}")
    private String testValue;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RESOURCE_QUEUE, arguments = {
                    @Argument(name = DLQ_EXCHANGE_ARG, value = RESOURCE_EXCHANGE_DLQ),
                    @Argument(name = DLQ_ROUTING_KEY_ARG, value = RESOURCE_ROUTING_KEY_DLQ)}),
            key = RESOURCE_ROUTING_KEY,
            exchange = @Exchange(value = RESOURCE_EXCHANGE, type = RESOURCE_EXCHANGE_TYPE)
    ))
    public void consumeResourceFromQueue(ResourceLink resourceLink) throws TikaException, IOException, SAXException {
        log.info("Invoking Resource consumer.");

        Long resourceId = resourceLink.getId();

        log.info("Getting resource from resource service with ID: {}.", resourceId);
        byte[] resource = getRequestForResourceService(resourceId);

        SongRequest songRequest = songMetadataConverter.convertSongMetadata(resourceId, resource);
        log.info("Sending metadata to song service.");
        postRequestForSongService(songRequest);

        log.info(resourceLink.toString());
    }

    private byte[] getRequestForResourceService(Long resourceId) {
        ServiceInstance resourceServiceClient = loadBalancerClient.choose("resource-service");
        return restTemplate.getForObject(resourceServiceClient.getUri() + resourceServiceGetEndpoint, byte[].class, resourceId);
    }

    private void postRequestForSongService(SongRequest songRequest) {
        ServiceInstance songServiceClient = loadBalancerClient.choose("song-service");
        restTemplate.postForObject(songServiceClient.getUri() + songServiceEndpoint, songRequest, Object.class);
    }

    @GetMapping
    public String testConfig() {
        return testValue;
    }
}
