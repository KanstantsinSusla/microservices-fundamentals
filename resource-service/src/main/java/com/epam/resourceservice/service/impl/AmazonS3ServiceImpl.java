package com.epam.resourceservice.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.epam.resourceservice.service.AmazonS3Service;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
@Transactional
public class AmazonS3ServiceImpl implements AmazonS3Service {
    private static final String SONG_BUCKET_NAME = "songs";

    @Autowired
    private AmazonS3 amazonS3;

    @Override
    public String addResource(byte[] data, String resourceKey) {
        if (!amazonS3.doesBucketExistV2(SONG_BUCKET_NAME)) {
            amazonS3.createBucket(SONG_BUCKET_NAME);
        }

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(data.length);
        amazonS3.putObject(SONG_BUCKET_NAME, resourceKey, new ByteArrayInputStream(data), objectMetadata);

        return SONG_BUCKET_NAME;
    }

    @Override
    public byte[] getResource(String bucketName, String resourceKey) throws IOException {
        if (!amazonS3.doesBucketExistV2(bucketName) || !amazonS3.doesObjectExist(bucketName, resourceKey)) {
            return null;
        }
        S3Object resource = amazonS3.getObject(bucketName, resourceKey);
        return IOUtils.toByteArray(resource.getObjectContent());
    }

    @Override
    public boolean deleteResource(String bucketName, String resourceKey) {
        if (amazonS3.doesBucketExistV2(bucketName) || amazonS3.doesObjectExist(bucketName, resourceKey)) {
            amazonS3.deleteObject(bucketName, resourceKey);
            return true;
        }
        return false;
    }
}
