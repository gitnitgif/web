package com.fjdialect.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fjdialect.entity.FolkResource;
import com.fjdialect.mapper.FolkResourceMapper;
import com.fjdialect.service.FolkResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FolkResourceServiceImpl implements FolkResourceService {

    @Autowired
    private FolkResourceMapper folkResourceMapper;

    @Override
    public List<FolkResource> getAllResources() {       //查询民俗志愿
        return folkResourceMapper.selectList(new LambdaQueryWrapper<FolkResource>()
                .orderByDesc(FolkResource::getCreateTime));
    }

    @Override
    public List<FolkResource> getResourcesByCityCode(String cityCode) {         //根据城市id检索
        return folkResourceMapper.selectByCityCode(cityCode);
    }

    @Override
    public List<FolkResource> getResourcesByType(String type) {         //按照资源类型检索
        return folkResourceMapper.selectList(new LambdaQueryWrapper<FolkResource>()
                .eq(FolkResource::getType, type)
                .orderByDesc(FolkResource::getCreateTime));
    }

    @Override
    public FolkResource getResourceById(Long id) {          //安照id检索
        return folkResourceMapper.selectById(id);
    }

    @Override
    public void addResource(FolkResource resource) {        //新增民俗资源
        resource.setCreateTime(LocalDateTime.now());
        resource.setUpdateTime(LocalDateTime.now());
        resource.setViewCount(0);
        folkResourceMapper.insert(resource);
    }

    @Override
    public void updateResource(FolkResource resource) {         //更新民俗资源
        resource.setUpdateTime(LocalDateTime.now());
        folkResourceMapper.updateById(resource);
    }

    @Override
    public void deleteResource(Long id) {       //根据id检索删除
        folkResourceMapper.deleteById(id);
    }

    @Override
    public void incrementViewCount(Long id) {       //资源浏览量
        FolkResource resource = folkResourceMapper.selectById(id);
        if (resource != null) {
            resource.setViewCount(resource.getViewCount() + 1);
            folkResourceMapper.updateById(resource);
        }
    }
}
