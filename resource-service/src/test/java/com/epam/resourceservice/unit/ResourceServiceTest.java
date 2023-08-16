package com.epam.resourceservice.unit;

import com.epam.resourceservice.dao.ResourceDao;
import com.epam.resourceservice.model.Resource;
import com.epam.resourceservice.service.AmazonS3Service;
import com.epam.resourceservice.service.ResourceService;
import com.epam.resourceservice.service.impl.ResourceServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

public class ResourceServiceTest {
    @Mock
    private AmazonS3Service amazonS3Service;
    @Mock
    private ResourceDao resourceDao;
    @Mock
    private Resource resource;

    private ResourceService resourceService;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        this.resourceService = new ResourceServiceImpl();
        when(resource.getId()).thenReturn(1L);

        ReflectionTestUtils.setField(resourceService, "amazonS3Service", amazonS3Service);
        ReflectionTestUtils.setField(resourceService, "resourceDao", resourceDao);
    }

    @Test
    public void addResourceTest() {
        String bucketName = "songs";
        byte[] data = new byte[]{};
        String resourceKey = "1";

        when(amazonS3Service.addResource(any(), any())).thenReturn(bucketName);
        when(resourceDao.save(any())).thenReturn(resource);

        Long resourceId = resourceService.addResource(data, resourceKey);

        Assert.assertEquals(Long.valueOf(1), resourceId);
    }

    @Test
    public void addResourceIncorrectArguments() {
        byte[] data = null;
        String resourceKey = "";

        Long resourceId = resourceService.addResource(data, resourceKey);

        Assert.assertNull(resourceId);
    }
}
