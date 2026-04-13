package com.fjdialect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fjdialect.entity.Quiz;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QuizMapper extends BaseMapper<Quiz> {
    
    List<Quiz> selectByCityCode(String cityCode);
    
    List<Quiz> selectByCategory(String category);
    
    List<Quiz> selectRandom(@Param("cityCode") String cityCode, @Param("limit") Integer limit);
}
