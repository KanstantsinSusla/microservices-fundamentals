package com.epam.resourceservice.service;

import java.io.IOException;
import java.util.List;

public interface ResourceService {
    Long addResource(byte[] data, String resourceKey);
    byte[] getResourceById(Long id) throws IOException;
    List<Long> deleteResourcesByIds(List<Long> ids);
}
