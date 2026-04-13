package com.fjdialect.service;

import com.fjdialect.entity.Audio;

import java.util.List;

/**
 * 音频 Service 接口
 */
public interface AudioService {
    
    /**
     * 上传音频
     */
    boolean uploadAudio(Audio audio);
    
    /**
     * 获取已审核通过的音频列表
     */
    List<Audio> getApprovedAudioList();
    
    /**
     * 获取待审核的音频列表
     */
    List<Audio> getPendingAudioList();
    
    /**
     * 获取所有音频列表（管理员用）
     */
    List<Audio> getAllAudioList();
    
    /**
     * 根据状态获取音频列表
     */
    List<Audio> getAudioListByStatus(String status);
    
    /**
     * 获取用户的音频列表
     */
    List<Audio> getUserAudioList(Long uploaderId);
    
    /**
     * 审核通过音频
     */
    boolean approveAudio(Long id);
    
    /**
     * 审核拒绝音频
     */
    boolean rejectAudio(Long id, String reason);
    
    /**
     * 删除音频
     */
    boolean deleteAudio(Long id);
    
    /**
     * 点赞音频
     */
    boolean rateAudio(Long id);
}
