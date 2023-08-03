package com.epam.resourceservice.service;

import java.io.IOException;

public interface AmazonS3Service {
    String addResource(byte[] data, String resourceKey);
    byte[] getResource(String bucketName, String resourceKey) throws IOException;
    boolean deleteResource(String bucketName, String resourceKey);
}
