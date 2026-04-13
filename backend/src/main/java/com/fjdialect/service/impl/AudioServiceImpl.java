package com.fjdialect.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fjdialect.entity.Audio;
import com.fjdialect.mapper.AudioMapper;
import com.fjdialect.service.AudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 音频 Service 实现类
 */
@Service
public class AudioServiceImpl implements AudioService {
    
    @Autowired
    private AudioMapper audioMapper;
    
    @Override
    public boolean uploadAudio(Audio audio) {
        audio.setCreateTime(LocalDateTime.now());
        audio.setUpdateTime(LocalDateTime.now());
        audio.setStatus("pending");  // 默认待审核状态
        audio.setRating(0);
        return audioMapper.insert(audio) > 0;
    }
    
    @Override
    public List<Audio> getApprovedAudioList() {
        LambdaQueryWrapper<Audio> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Audio::getStatus, "approved")
               .orderByDesc(Audio::getCreateTime);
        return audioMapper.selectList(wrapper);
    }
    
    @Override
    public List<Audio> getPendingAudioList() {
        LambdaQueryWrapper<Audio> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Audio::getStatus, "pending")
               .orderByDesc(Audio::getCreateTime);
        return audioMapper.selectList(wrapper);
    }
    
    @Override
    public List<Audio> getAllAudioList() {
        LambdaQueryWrapper<Audio> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Audio::getCreateTime);
        return audioMapper.selectList(wrapper);
    }
    
    @Override
    public List<Audio> getAudioListByStatus(String status) {
        LambdaQueryWrapper<Audio> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Audio::getStatus, status)
               .orderByDesc(Audio::getCreateTime);
        return audioMapper.selectList(wrapper);
    }
    
    @Override
    public List<Audio> getUserAudioList(Long uploaderId) {
        LambdaQueryWrapper<Audio> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Audio::getUploaderId, uploaderId)
               .orderByDesc(Audio::getCreateTime);
        return audioMapper.selectList(wrapper);
    }
    
    @Override
    public boolean approveAudio(Long id) {
        Audio audio = audioMapper.selectById(id);
        if (audio == null) return false;
        
        audio.setStatus("approved");
        audio.setUpdateTime(LocalDateTime.now());
        return audioMapper.updateById(audio) > 0;
    }
    
    @Override
    public boolean rejectAudio(Long id, String reason) {
        Audio audio = audioMapper.selectById(id);
        if (audio == null) return false;
        
        audio.setStatus("rejected");
        audio.setRejectReason(reason);
        audio.setUpdateTime(LocalDateTime.now());
        return audioMapper.updateById(audio) > 0;
    }
    
    @Override
    public boolean deleteAudio(Long id) {
        return audioMapper.deleteById(id) > 0;
    }
    
    @Override
    public boolean rateAudio(Long id) {
        Audio audio = audioMapper.selectById(id);
        if (audio == null) return false;
        
        audio.setRating(audio.getRating() + 1);
        return audioMapper.updateById(audio) > 0;
    }
}
