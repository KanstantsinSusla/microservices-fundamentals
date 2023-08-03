package com.epam.resourceprocessor.controller;

import lombok.extern.log4j.Log4j2;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/resource-processor")
@Log4j2
public class ResourceProcessorController {

    @PostMapping
    public void handleResource(InputStream inputStream) throws TikaException, IOException, SAXException {
        log.info("Process resource handling.");

        Metadata metadata = new Metadata();
        Mp3Parser parser = new Mp3Parser();
        BodyContentHandler handler = new BodyContentHandler();

        parser.parse(inputStream, handler, metadata, new ParseContext());

        String format = metadata.get("xmpDM:audioCompressor");
        log.info("Format: {}", format);

        String title = metadata.get("title");
        log.info("Title: {}", title);

        String artist = metadata.get("xmpDM:artist");
        log.info("Artist: {}", artist);

        String album = metadata.get("xmpDM:album");
        log.info("Album: {}", album);

        String duration = metadata.get("xmpDM:duration");
        log.info("Duration: {}", duration);

        String releaseDate = metadata.get("xmpDM:releaseDate");
        log.info("Release date: {}", releaseDate);
    }
}
