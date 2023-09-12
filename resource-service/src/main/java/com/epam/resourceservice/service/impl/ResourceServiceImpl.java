package com.epam.resourceservice.service.impl;

import com.epam.resourceservice.dao.ResourceDao;
import com.epam.resourceservice.model.Resource;
import com.epam.resourceservice.model.StorageDetails;
import com.epam.resourceservice.service.AmazonS3Service;
import com.epam.resourceservice.service.ResourceService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Log4j2
public class ResourceServiceImpl implements ResourceService {
    @Autowired
    private ResourceDao resourceDao;

    @Autowired
    private AmazonS3Service amazonS3Service;

    @Override
    public Long addResource(byte[] data, String resourceKey, StorageDetails storageDetails) {
        String resourceKeyWithPath = storageDetails.getPath().concat(resourceKey);

        String bucketName = amazonS3Service.addResource(data, storageDetails.getBucket(), resourceKeyWithPath);

        if (StringUtils.isBlank(bucketName) || StringUtils.isBlank(resourceKey)) {
            return null;
        }

        Resource resource = new Resource();
        resource.setBucketName(bucketName);
        resource.setResourceKey(resourceKeyWithPath);

        return resourceDao.save(resource).getId();
    }

    @Override
    public Long updateResource(String resourceKey, StorageDetails stagingDetails,
                               StorageDetails permanentDetails, Long resourceId) throws IOException {
        String stagingResourceKeyWithPath = stagingDetails.getPath().concat(resourceKey);
        byte[] data = amazonS3Service.getResource(stagingDetails.getBucket(), stagingResourceKeyWithPath);

        amazonS3Service.deleteResource(stagingDetails.getBucket(),
                stagingDetails.getPath().concat(resourceKey));

        String permanentResourceKeyWithPath = permanentDetails.getPath().concat(resourceKey);
        String permanentBucketName = amazonS3Service.addResource(data, permanentDetails.getBucket(), permanentResourceKeyWithPath);

        Resource resource = resourceDao.findById(resourceId).orElse(null);

        if (resource == null) {
            throwNotFoundException(resourceId);
        }

        resource.setBucketName(permanentBucketName);
        resource.setResourceKey(permanentResourceKeyWithPath);

        return resourceDao.save(resource).getId();
    }

    @Override
    public byte[] getResourceById(Long id) throws IOException {
        Resource resource = resourceDao.findById(id).orElse(null);

        if (resource == null) {
            throwNotFoundException(id);
        }

        byte[] data = amazonS3Service.getResource(resource.getBucketName(), resource.getResourceKey());

        if (data == null || data.length == 0) {
            throwNotFoundException(id);
        }

        return data;
    }

    @Override
    public List<Long> deleteResourcesByIds(List<Long> ids) {
        return ids.stream()
                .filter(id -> resourceDao.existsById(id))
                .map(id -> resourceDao.findById(id).get())
                .filter(resource -> amazonS3Service.deleteResource(resource.getBucketName(), resource.getResourceKey()))
                .peek(resource -> resourceDao.deleteById(resource.getId()))
                .map(Resource::getId)
                .collect(Collectors.toList());
    }

    private void throwNotFoundException(Long resourceId) {
        log.error("Resource with id: {} was not fount.", resourceId);
        throw new ResourceNotFoundException("The resource with the specified id does not exist.");
    }
}
