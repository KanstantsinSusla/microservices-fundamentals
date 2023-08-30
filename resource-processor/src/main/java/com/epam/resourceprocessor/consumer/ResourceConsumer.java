package com.epam.resourceprocessor.consumer;

import com.epam.resourceprocessor.converter.SongMetadataConverter;
import com.epam.resourceprocessor.model.ResourceLink;
import com.epam.resourceprocessor.model.SongRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.tika.exception.TikaException;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;

import java.io.IOException;

@Log4j2
@Component
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
    private RestTemplate restTemplate;

    @Autowired
    private SongMetadataConverter songMetadataConverter;

    @Value("${resource.service.url}")
    private String resourceServiceUrl;

    @Value("${song.service.url}")
    private String songServiceUrl;

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
        byte[] resource = restTemplate.getForObject(resourceServiceUrl, byte[].class, resourceId);
        SongRequest songRequest = songMetadataConverter.convertSongMetadata(resourceId, resource);

        log.info("Sending metadata to song service.");
        restTemplate.postForObject(songServiceUrl, songRequest, Object.class);

        log.info(resourceLink.toString());
    }
}
