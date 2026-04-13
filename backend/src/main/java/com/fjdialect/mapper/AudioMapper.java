package com.fjdialect.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fjdialect.entity.Audio;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 音频 Mapper 接口
 */
@Mapper
public interface AudioMapper extends BaseMapper<Audio> {
    
    /**
     * 获取已审核通过的音频列表
     */
    List<Audio> getApprovedAudioList();
    
    /**
     * 获取待审核的音频列表
     */
    List<Audio> getPendingAudioList();
    
    /**
     * 获取用户的音频列表
     */
    List<Audio> getUserAudioList(Long uploaderId);
}
