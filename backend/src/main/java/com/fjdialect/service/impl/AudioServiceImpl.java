package com.fjdialect.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fjdialect.entity.Audio;
import com.fjdialect.mapper.AudioMapper;
import com.fjdialect.service.AudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AudioServiceImpl implements AudioService {
    
    @Autowired
    private AudioMapper audioMapper;
    
    @Override
    public boolean uploadAudio(Audio audio) {       //上传音频
        audio.setCreateTime(LocalDateTime.now());
        audio.setUpdateTime(LocalDateTime.now());
        audio.setStatus("pending");  // 默认待审核状态
        audio.setRating(0);
        return audioMapper.insert(audio) > 0;
    }
    
    @Override
    public List<Audio> getApprovedAudioList() {   //审核通过音频
        LambdaQueryWrapper<Audio> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Audio::getStatus, "approved")
               .orderByDesc(Audio::getCreateTime);
        return audioMapper.selectList(wrapper);
    }
    
    @Override
    public List<Audio> getPendingAudioList() {      //待审核
        LambdaQueryWrapper<Audio> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Audio::getStatus, "pending")
               .orderByDesc(Audio::getCreateTime);
        return audioMapper.selectList(wrapper);
    }
    
    @Override
    public List<Audio> getAllAudioList() {      //检索所有音频
        LambdaQueryWrapper<Audio> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Audio::getCreateTime);
        return audioMapper.selectList(wrapper);
    }
    
    @Override
    public List<Audio> getAudioListByStatus(String status) {        //查询音频状态
        LambdaQueryWrapper<Audio> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Audio::getStatus, status)
               .orderByDesc(Audio::getCreateTime);
        return audioMapper.selectList(wrapper);
    }
    
    @Override
    public List<Audio> getUserAudioList(Long uploaderId) {          //检索用户上传音频
        LambdaQueryWrapper<Audio> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Audio::getUploaderId, uploaderId)
               .orderByDesc(Audio::getCreateTime);
        return audioMapper.selectList(wrapper);
    }
    
    @Override
    public boolean approveAudio(Long id) {      //已通过音频
        Audio audio = audioMapper.selectById(id);
        if (audio == null) return false;
        
        audio.setStatus("approved");
        audio.setUpdateTime(LocalDateTime.now());
        return audioMapper.updateById(audio) > 0;
    }
    
    @Override
    public boolean rejectAudio(Long id, String reason) {    //未通过
        Audio audio = audioMapper.selectById(id);
        if (audio == null) return false;
        
        audio.setStatus("rejected");
        audio.setRejectReason(reason);
        audio.setUpdateTime(LocalDateTime.now());
        return audioMapper.updateById(audio) > 0;
    }
    
    @Override
    public boolean deleteAudio(Long id) {       //删除音频
        return audioMapper.deleteById(id) > 0;
    }
    
    @Override
    public boolean rateAudio(Long id) {         //评分
        Audio audio = audioMapper.selectById(id);
        if (audio == null) return false;
        
        audio.setRating(audio.getRating() + 1);
        return audioMapper.updateById(audio) > 0;
    }
}
