package com.epam.storageservice.service.impl;

import com.epam.storageservice.dao.StorageDAO;
import com.epam.storageservice.model.Storage;
import com.epam.storageservice.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class StorageServiceImpl implements StorageService {
    @Autowired
    private StorageDAO storageDAO;

    @Override
    public Long addStorage(Storage storage) {
        return storageDAO.save(storage).getId();
    }

    @Override
    public List<Storage> getAllStorages() {
        return StreamSupport.stream(storageDAO.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Storage getStorageByType(String type) {
        return storageDAO.findByStorageType(type);
    }

    @Override
    public List<Long> deleteStoragesByIds(List<Long> ids) {
        return ids.stream()
                .filter(id -> storageDAO.existsById(id))
                .peek(id -> storageDAO.deleteById(id))
                .collect(Collectors.toList());
    }
}
