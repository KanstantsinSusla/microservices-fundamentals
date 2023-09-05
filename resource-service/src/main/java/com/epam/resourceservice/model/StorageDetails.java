package com.epam.resourceservice.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StorageDetails {
    private String bucket;
    private String path;
}
