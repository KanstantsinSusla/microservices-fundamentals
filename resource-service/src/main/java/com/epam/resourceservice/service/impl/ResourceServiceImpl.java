package com.epam.resourceservice.service.impl;

import com.epam.resourceservice.dao.ResourceDao;
import com.epam.resourceservice.model.Resource;
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
    public Long addResource(byte[] data, String resourceKey) {
        String bucketName = amazonS3Service.addResource(data, resourceKey);

        if (StringUtils.isBlank(bucketName) || StringUtils.isBlank(resourceKey)) {
            return null;
        }

        Resource resource = new Resource();
        resource.setBucketName(bucketName);
        resource.setResourceKey(resourceKey);

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
