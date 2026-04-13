package com.fjdialect.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fjdialect.entity.CityResource;

import java.util.List;

public interface CityResourceService extends IService<CityResource> {
    List<CityResource> getAllResources();
    CityResource getResourceByCode(String code);
    boolean saveResource(CityResource resource);
    boolean updateResource(CityResource resource);
    boolean deleteResource(Long id);
}
