package com.fjdialect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fjdialect.entity.FolkResource;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FolkResourceMapper extends BaseMapper<FolkResource> {
    
    List<FolkResource> selectByCityCode(String cityCode);
    
    List<FolkResource> selectByType(String type);
}
