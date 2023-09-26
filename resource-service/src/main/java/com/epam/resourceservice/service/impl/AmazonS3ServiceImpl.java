package com.epam.resourceservice.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.epam.resourceservice.service.AmazonS3Service;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
@Transactional
@Log4j2
public class AmazonS3ServiceImpl implements AmazonS3Service {

    @Autowired
    private AmazonS3 amazonS3;

    @Override
    public String addResource(byte[] data, String bucketName, String resourceKey) {
        log.info("Adding resource with bucket: {} and resource key: {}.", bucketName, resourceKey);

        if (!amazonS3.doesBucketExistV2(bucketName)) {
            amazonS3.createBucket(bucketName);
        }

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(data.length);
        amazonS3.putObject(bucketName, resourceKey, new ByteArrayInputStream(data), objectMetadata);

        return bucketName;
    }

    @Override
    public byte[] getResource(String bucketName, String resourceKey) throws IOException {
        log.info("Getting resource with bucket: {} and resource key: {}.", bucketName, resourceKey);

        if (!amazonS3.doesBucketExistV2(bucketName) || !amazonS3.doesObjectExist(bucketName, resourceKey)) {
            return null;
        }
        S3Object resource = amazonS3.getObject(bucketName, resourceKey);
        return IOUtils.toByteArray(resource.getObjectContent());
    }

    @Override
    public boolean deleteResource(String bucketName, String resourceKey) {
        log.info("Deleting resource with bucket: {} and resource key: {}.", bucketName, resourceKey);

        if (amazonS3.doesBucketExistV2(bucketName) || amazonS3.doesObjectExist(bucketName, resourceKey)) {
            amazonS3.deleteObject(bucketName, resourceKey);
            return true;
        }
        return false;
    }
}
