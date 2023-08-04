package com.epam.resourceservice.controller;

import com.epam.resourceservice.exception.ResourceValidationException;
import com.epam.resourceservice.service.ResourceService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
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

        Long resourceId;

        if (metadata.get("xmpDM:audioCompressor").equalsIgnoreCase("mp3")) {

            resourceId = resourceService.addResource(IOUtils.toByteArray(secondClone), String.valueOf(metadata.hashCode()));
        } else {
            throw new ResourceValidationException("Validation failed or request body is invalid MP3.");
        }

        return Collections.singletonMap("id", resourceId);
    }

    @DeleteMapping()
    public List<Long> deleteResource(@RequestParam (value = "ids") @Size(max = 200) List<Long> ids) {
        log.info("Process delete resource.");
        return resourceService.deleteResourcesByIds(ids);
    }
}
