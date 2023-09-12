package com.epam.storageservice.controller;

import com.epam.storageservice.model.Storage;
import com.epam.storageservice.service.StorageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/storages")
public class StorageController {
    @Autowired
    private StorageService storageService;

    @PostMapping
    public Map<String, Long> createStorage(@RequestBody Storage storage) {
        log.info("Process adding a storage.");
        return Collections.singletonMap("id", storageService.addStorage(storage));
    }

    @GetMapping
    public List<Storage> getAllStorages() {
        log.info("Process getting all storages.");
        return storageService.getAllStorages();
    }

    @GetMapping(value = "/{type}")
    public Storage getStorageByType(@PathVariable("type") String type) {
        log.info("Process getting storage by type.");
        return storageService.getStorageByType(type);
    }

    @DeleteMapping
    public List<Long> deleteStorages(@RequestParam (value = "ids") @Size(max = 200) List<Long> ids) {
        log.info("Process deleting storages.");
        return storageService.deleteStoragesByIds(ids);
    }

}
