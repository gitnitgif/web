package com.fjdialect.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fjdialect.entity.DialectResource;
import com.fjdialect.mapper.DialectResourceMapper;
import com.fjdialect.service.DialectResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DialectResourceServiceImpl implements DialectResourceService {

    @Autowired
    private DialectResourceMapper dialectResourceMapper;

    @Override
    public List<DialectResource> getAllResources() {
        return dialectResourceMapper.selectList(new LambdaQueryWrapper<DialectResource>()
                .orderByDesc(DialectResource::getCreateTime));
    }

    @Override
    public List<DialectResource> getResourcesByCityCode(String cityCode) {
        return dialectResourceMapper.selectByCityCode(cityCode);
    }

    @Override
    public List<DialectResource> getResourcesByCategory(String category) {
        return dialectResourceMapper.selectList(new LambdaQueryWrapper<DialectResource>()
                .eq(DialectResource::getCategory, category)
                .orderByDesc(DialectResource::getCreateTime));
    }

    @Override
    public List<DialectResource> getResourcesByDifficulty(String difficulty) {
        return dialectResourceMapper.selectList(new LambdaQueryWrapper<DialectResource>()
                .eq(DialectResource::getDifficulty, difficulty)
                .orderByDesc(DialectResource::getCreateTime));
    }

    @Override
    public DialectResource getResourceById(Long id) {
        return dialectResourceMapper.selectById(id);
    }

    @Override
    public void addResource(DialectResource resource) {
        resource.setCreateTime(LocalDateTime.now());
        resource.setUpdateTime(LocalDateTime.now());
        resource.setViewCount(0);
        dialectResourceMapper.insert(resource);
    }

    @Override
    public void updateResource(DialectResource resource) {
        resource.setUpdateTime(LocalDateTime.now());
        dialectResourceMapper.updateById(resource);
    }

    @Override
    public void deleteResource(Long id) {
        dialectResourceMapper.deleteById(id);
    }

    @Override
    public void incrementViewCount(Long id) {
        DialectResource resource = dialectResourceMapper.selectById(id);
        if (resource != null) {
            resource.setViewCount(resource.getViewCount() + 1);
            dialectResourceMapper.updateById(resource);
        }
    }
}
