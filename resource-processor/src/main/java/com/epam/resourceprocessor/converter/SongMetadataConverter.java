package com.epam.resourceprocessor.converter;

import com.epam.resourceprocessor.model.SongRequest;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.IOException;

public interface SongMetadataConverter {
    public SongRequest convertSongMetadata(Long resourceId, byte[] resource) throws TikaException, IOException, SAXException;
}
