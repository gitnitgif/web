package com.fjdialect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fjdialect.entity.QuizRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuizRecordMapper extends BaseMapper<QuizRecord> {
    
    List<QuizRecord> selectByUserId(Long userId);
}
