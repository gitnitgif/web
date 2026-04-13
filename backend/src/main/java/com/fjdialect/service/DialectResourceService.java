package com.fjdialect.service;

import com.fjdialect.entity.DialectResource;
import java.util.List;

public interface DialectResourceService {
    List<DialectResource> getAllResources();
    List<DialectResource> getResourcesByCityCode(String cityCode);
    List<DialectResource> getResourcesByCategory(String category);
    List<DialectResource> getResourcesByDifficulty(String difficulty);
    DialectResource getResourceById(Long id);
    void addResource(DialectResource resource);
    void updateResource(DialectResource resource);
    void deleteResource(Long id);
    void incrementViewCount(Long id);
}
