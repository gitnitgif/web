package com.fjdialect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fjdialect.entity.DialectResource;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DialectResourceMapper extends BaseMapper<DialectResource> {
    
    List<DialectResource> selectByCityCode(String cityCode);
    
    List<DialectResource> selectByCategory(String category);
    
    List<DialectResource> selectByDifficulty(String difficulty);
}
