package com.epam.resourceservice.service.impl;

import com.epam.resourceservice.model.StorageDetails;
import com.epam.resourceservice.service.StorageService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Log4j2
public class StorageServiceImpl implements StorageService {
    private static final String STAGING_STORAGE = "STAGING";
    private static final String PERMANENT_STORAGE = "PERMANENT";

    @Value("${storage.service.endpoint}")
    private String storageServiceEndpoint;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    @CircuitBreaker(name = "storage-service-staging", fallbackMethod = "getFallbackStagingDetails")
    @Override
    public StorageDetails getRequestForStagingDetails() {
        ServiceInstance resourceServiceClient = loadBalancerClient.choose("storage-service");
        return restTemplate.getForObject(resourceServiceClient.getUri() + storageServiceEndpoint, StorageDetails.class, STAGING_STORAGE);
    }

    private StorageDetails getFallbackStagingDetails(Exception e) {
        log.info("Staging fallback method was invoked.");
        return new StorageDetails("staging", "songs/");
    }

    @CircuitBreaker(name = "storage-service-permanent", fallbackMethod = "getFallbackPermanentDetails")
    @Override
    public StorageDetails getRequestForPermanentDetails() {
        ServiceInstance resourceServiceClient = loadBalancerClient.choose("storage-service");
        return restTemplate.getForObject(resourceServiceClient.getUri() + storageServiceEndpoint, StorageDetails.class, PERMANENT_STORAGE);
    }

    private StorageDetails getFallbackPermanentDetails(Exception e) {
        log.info("Permanent fallback method was invoked.");
        return new StorageDetails("permanent", "songs/");
    }
}
