package com.epam.storageservice.dao;

import com.epam.storageservice.model.Storage;
import org.springframework.data.repository.CrudRepository;

public interface StorageDAO extends CrudRepository<Storage, Long> {
    Storage findByStorageType(String type);
}
