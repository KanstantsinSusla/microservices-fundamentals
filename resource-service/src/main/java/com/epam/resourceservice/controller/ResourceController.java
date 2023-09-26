package com.epam.resourceservice.controller;

import com.epam.resourceservice.config.MessagingConfig;
import com.epam.resourceservice.exception.ResourceValidationException;
import com.epam.resourceservice.model.StorageDetails;
import com.epam.resourceservice.service.ResourceService;
import com.epam.resourceservice.service.StorageService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.validation.constraints.Size;
import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/resources")
@Validated
@Log4j2
public class ResourceController {
    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private StorageService storageService;

    @GetMapping(value = "/{id}")
    public byte[] getResourceById(@PathVariable ("id") Long id) throws IOException {
        log.info("Process get resource by id.");
        return resourceService.getResourceById(id);
    }

    @PostMapping()
    public Map<String, Long> addResource(InputStream inputStream) throws IOException, TikaException, SAXException {
        log.info("Process add resource.");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        inputStream.transferTo(baos);
        InputStream firstClone = new ByteArrayInputStream(baos.toByteArray());
        InputStream secondClone = new ByteArrayInputStream(baos.toByteArray());

        Metadata metadata = new Metadata();
        Mp3Parser parser = new Mp3Parser();
        BodyContentHandler handler = new BodyContentHandler();

        parser.parse(firstClone, handler, metadata, new ParseContext());

        if (!metadata.get("xmpDM:audioCompressor").equalsIgnoreCase("mp3")) {
            throw new ResourceValidationException("Validation failed or request body is invalid MP3.");
        }

        String resourceKey = String.valueOf(metadata.hashCode());

        StorageDetails stagingDetails = storageService.getRequestForStagingDetails();
        Long stagingResourceId = resourceService.addResource(IOUtils.toByteArray(secondClone), resourceKey, stagingDetails);

        Map<String, Long> queueRequest = Collections.singletonMap("id", stagingResourceId);

        sendMessage(queueRequest);

        StorageDetails permanentDetails = storageService.getRequestForPermanentDetails();
        Long permanentResourceId = resourceService.updateResource(resourceKey, stagingDetails, permanentDetails, stagingResourceId);

        return Collections.singletonMap("id", permanentResourceId);
    }

    @DeleteMapping()
    public List<Long> deleteResource(@RequestParam (value = "ids") @Size(max = 200) List<Long> ids) {
        log.info("Process delete resource.");
        return resourceService.deleteResourcesByIds(ids);
    }

    public void sendMessage(Map<String, Long> messageBody) {
        template.convertAndSend(MessagingConfig.RESOURCE_EXCHANGE, MessagingConfig.RESOURCE_ROUTING_KEY, messageBody, new CorrelationData());
    }
}
