package com.fjdialect.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fjdialect.entity.CityResource;
import com.fjdialect.mapper.CityResourceMapper;
import com.fjdialect.service.CityResourceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityResourceServiceImpl extends ServiceImpl<CityResourceMapper, CityResource> implements CityResourceService {
    
    @Override
    public List<CityResource> getAllResources() {
        LambdaQueryWrapper<CityResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(CityResource::getCode);
        return baseMapper.selectList(wrapper);
    }
    
    @Override
    public CityResource getResourceByCode(String code) {
        LambdaQueryWrapper<CityResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CityResource::getCode, code);
        return baseMapper.selectOne(wrapper);
    }
    
    @Override
    public boolean saveResource(CityResource resource) {
        return save(resource);
    }
    
    @Override
    public boolean updateResource(CityResource resource) {
        return updateById(resource);
    }
    
    @Override
    public boolean deleteResource(Long id) {
        return removeById(id);
    }
}
