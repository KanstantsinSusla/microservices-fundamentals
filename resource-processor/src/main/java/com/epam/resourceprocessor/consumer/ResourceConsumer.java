package com.epam.resourceprocessor.consumer;

import com.epam.resourceprocessor.converter.SongMetadataConverter;
import com.epam.resourceprocessor.model.ResourceLink;
import com.epam.resourceprocessor.model.SongRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.tika.exception.TikaException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;

import java.io.IOException;

@Log4j2
@Component
public class ResourceConsumer {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SongMetadataConverter songMetadataConverter;

    @Value("${resource.service.url}")
    private String resourceServiceUrl;

    @Value("${song.service.url}")
    private String songServiceUrl;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "resource.queue"),
            key = "resource_routing_key",
            exchange = @Exchange(value = "resource_exchange")
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
