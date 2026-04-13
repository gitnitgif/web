package com.fjdialect.service;

import com.fjdialect.entity.FolkResource;
import java.util.List;

public interface FolkResourceService {
    List<FolkResource> getAllResources();
    List<FolkResource> getResourcesByCityCode(String cityCode);
    List<FolkResource> getResourcesByType(String type);
    FolkResource getResourceById(Long id);
    void addResource(FolkResource resource);
    void updateResource(FolkResource resource);
    void deleteResource(Long id);
    void incrementViewCount(Long id);
}
