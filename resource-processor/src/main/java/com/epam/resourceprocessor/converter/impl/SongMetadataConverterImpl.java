package com.epam.resourceprocessor.converter.impl;

import com.epam.resourceprocessor.converter.SongMetadataConverter;
import com.epam.resourceprocessor.model.SongRequest;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class SongMetadataConverterImpl implements SongMetadataConverter {

    @Override
    public SongRequest convertSongMetadata(Long resourceId, byte[] resource) throws TikaException, IOException, SAXException {
        InputStream inputStream = new ByteArrayInputStream(resource);

        Metadata metadata = new Metadata();
        Mp3Parser parser = new Mp3Parser();
        BodyContentHandler handler = new BodyContentHandler();

        parser.parse(inputStream, handler, metadata, new ParseContext());

        SongRequest songRequest = new SongRequest();

        songRequest.setName(metadata.get("title"));
        songRequest.setArtist(metadata.get("xmpDM:artist"));
        songRequest.setAlbum(metadata.get("xmpDM:album"));
        songRequest.setLength(metadata.get("xmpDM:duration"));
        songRequest.setResourceId(resourceId);
        songRequest.setYear(metadata.get("xmpDM:releaseDate"));

        return songRequest;
    }
}
