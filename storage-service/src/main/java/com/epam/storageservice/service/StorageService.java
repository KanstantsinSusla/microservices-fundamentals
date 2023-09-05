package com.epam.storageservice.service;

import com.epam.storageservice.model.Storage;

import java.util.List;

public interface StorageService {
    Long addStorage(Storage storage);
    List<Storage> getAllStorages();
    Storage getStorageByType(String type);
    List<Long> deleteStoragesByIds(List<Long> ids);
}
