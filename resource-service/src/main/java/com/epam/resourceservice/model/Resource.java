package com.epam.resourceservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "BUCKET_NAME")
    private String bucketName;

    @Column(name = "RESOURCE_KEY")
    private String resourceKey;
}
