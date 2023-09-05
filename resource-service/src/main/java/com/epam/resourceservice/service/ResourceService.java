package com.epam.resourceservice.service;

import com.epam.resourceservice.model.StorageDetails;

import java.io.IOException;
import java.util.List;

public interface ResourceService {
    Long addResource(byte[] data, String resourceKey, StorageDetails storageDetails);
    Long updateResource(String resourceKey, StorageDetails stagingDetails, StorageDetails permanentDetails, Long resourceId) throws IOException;
    byte[] getResourceById(Long id) throws IOException;
    List<Long> deleteResourcesByIds(List<Long> ids);
}
