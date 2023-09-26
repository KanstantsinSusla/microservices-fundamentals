package com.epam.resourceservice.service;

import com.epam.resourceservice.model.StorageDetails;

public interface StorageService {
    StorageDetails getRequestForStagingDetails();
    StorageDetails getRequestForPermanentDetails();
}
