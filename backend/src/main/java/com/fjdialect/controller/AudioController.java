package com.fjdialect.controller;

import com.fjdialect.entity.Audio;
import com.fjdialect.service.AudioService;
import com.fjdialect.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * 音频 Controller
 */
@RestController
@RequestMapping("/api/audio")
@CrossOrigin(origins = "*")
public class AudioController {
    
    @Autowired
    private AudioService audioService;
    
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "audio" + File.separator;
    
    /**
     * 上传音频
     */
    @PostMapping("/upload")
    public Result<Audio> uploadAudio(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("cityCode") String cityCode,
            @RequestParam("dialectType") String dialectType,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("uploader") String uploader,
            @RequestParam("uploaderId") Long uploaderId
    ) {
        try {
            // 保存音频文件
            String fileName = saveAudioFile(file);
            
            // 创建音频对象
            Audio audio = new Audio();
            audio.setTitle(title);
            audio.setCityCode(cityCode);
            audio.setDialectType(dialectType);
            audio.setDescription(description);
            audio.setAudioUrl("/uploads/audio/" + fileName);
            audio.setUploader(uploader);
            audio.setUploaderId(uploaderId);
            
            // 保存到数据库
            boolean success = audioService.uploadAudio(audio);
            if (success) {
                return Result.success(audio);
            } else {
                return Result.error("上传失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取音频列表（已审核通过）
     */
    @GetMapping("/list")
    public Result<List<Audio>> getAudioList() {
        List<Audio> list = audioService.getApprovedAudioList();
        return Result.success(list);
    }
    
    /**
     * 获取音频详情
     */
    @GetMapping("/{id}")
    public Result<Audio> getAudioDetail(@PathVariable Long id) {
        Audio audio = audioService.getUserAudioList(id).stream().findFirst().orElse(null);
        if (audio != null) {
            return Result.success(audio);
        } else {
            return Result.error("音频不存在");
        }
    }
    
    /**
     * 点赞音频
     */
    @PostMapping("/{id}/rate")
    public Result<Void> rateAudio(@PathVariable Long id) {
        boolean success = audioService.rateAudio(id);
        return success ? Result.success(null) : Result.error("点赞失败");
    }
    
    /**
     * 删除音频
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteAudio(@PathVariable Long id) {
        boolean success = audioService.deleteAudio(id);
        return success ? Result.success(null) : Result.error("删除失败");
    }
    
    /**
     * 获取用户的音频列表
     */
    @GetMapping("/my")
    public Result<List<Audio>> getMyAudios(@RequestParam Long uploaderId) {
        List<Audio> list = audioService.getUserAudioList(uploaderId);
        return Result.success(list);
    }
    
    /**
     * 保存音频文件
     */
    private String saveAudioFile(MultipartFile file) throws IOException {
        // 创建上传目录
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".mp3";
        String fileName = UUID.randomUUID().toString() + extension;
        
        // 保存文件
        Path filePath = uploadPath.resolve(fileName);
        file.transferTo(filePath.toFile());
        
        return fileName;
    }
}
