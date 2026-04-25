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
    public List<DialectResource> getAllResources() {        //查询所有资源
        return dialectResourceMapper.selectList(new LambdaQueryWrapper<DialectResource>()
                .orderByDesc(DialectResource::getCreateTime));
    }

    @Override
    public List<DialectResource> getResourcesByCityCode(String cityCode) {      //依照城市id检索方言资源
        return dialectResourceMapper.selectByCityCode(cityCode);
    }

    @Override
    public List<DialectResource> getResourcesByCategory(String category) {      //依照资源分类查询
        return dialectResourceMapper.selectList(new LambdaQueryWrapper<DialectResource>()
                .eq(DialectResource::getCategory, category)
                .orderByDesc(DialectResource::getCreateTime));
    }

    @Override
    public List<DialectResource> getResourcesByDifficulty(String difficulty) {      //依照难度检索
        return dialectResourceMapper.selectList(new LambdaQueryWrapper<DialectResource>()
                .eq(DialectResource::getDifficulty, difficulty)
                .orderByDesc(DialectResource::getCreateTime));
    }

    @Override
    public DialectResource getResourceById(Long id) {       //依照id检索
        return dialectResourceMapper.selectById(id);
    }

    @Override
    public void addResource(DialectResource resource) {     //增加资源
        resource.setCreateTime(LocalDateTime.now());
        resource.setUpdateTime(LocalDateTime.now());
        resource.setViewCount(0);
        dialectResourceMapper.insert(resource);
    }

    @Override
    public void updateResource(DialectResource resource) {          //更新资源
        resource.setUpdateTime(LocalDateTime.now());
        dialectResourceMapper.updateById(resource);
    }

    @Override
    public void deleteResource(Long id) {       //删除资源
        dialectResourceMapper.deleteById(id);
    }

    @Override
    public void incrementViewCount(Long id) {       //浏览量
        DialectResource resource = dialectResourceMapper.selectById(id);
        if (resource != null) {
            resource.setViewCount(resource.getViewCount() + 1);
            dialectResourceMapper.updateById(resource);
        }
    }
}
