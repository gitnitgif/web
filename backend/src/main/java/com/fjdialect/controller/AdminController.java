package com.fjdialect.controller;

import com.fjdialect.entity.Audio;
import com.fjdialect.entity.User;
import com.fjdialect.service.AudioService;
import com.fjdialect.service.UserService;
import com.fjdialect.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private AudioService audioService;

    @GetMapping("/users")
    public Result<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return Result.success("获取成功", users);
    }

    @DeleteMapping("/user/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/system/info")
    public Result<Map<String, Object>> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        
        // JVM 信息
        info.put("javaVersion", System.getProperty("java.version"));
        info.put("javaVendor", System.getProperty("java.vendor"));
        info.put("javaHome", System.getProperty("java.home"));
        
        // 系统信息
        info.put("osName", System.getProperty("os.name"));
        info.put("osArch", System.getProperty("os.arch"));
        info.put("osVersion", System.getProperty("os.version"));
        
        // 内存信息
        Runtime runtime = Runtime.getRuntime();
        info.put("totalMemory", runtime.totalMemory() / (1024 * 1024) + " MB");
        info.put("freeMemory", runtime.freeMemory() / (1024 * 1024) + " MB");
        info.put("maxMemory", runtime.maxMemory() / (1024 * 1024) + " MB");
        info.put("availableProcessors", runtime.availableProcessors());
        
        // 用户信息
        info.put("userDir", System.getProperty("user.dir"));
        info.put("userHome", System.getProperty("user.home"));
        
        return Result.success("获取系统信息成功", info);
    }

    @GetMapping("/database/status")
    public Result<Map<String, Object>> getDatabaseStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("database", "MySQL");
        status.put("status", "Connected");
        status.put("userCount", userService.getAllUsers().size());
        return Result.success("获取数据库状态成功", status);
    }

    @PostMapping("/user/reset-password/{id}")
    public Result<String> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newPassword = body.get("newPassword");
        userService.resetPassword(id, newPassword);
        return Result.success("密码重置成功", null);
    }
    
    /**
     * 获取音频审核列表（支持按状态筛选）
     * @param status 状态：pending, approved, rejected（可选，不传则返回所有）
     */
    @GetMapping("/audio/review")
    public Result<List<Audio>> getAudioReviewList(@RequestParam(required = false) String status) {
        List<Audio> list;
        if (status != null && !status.isEmpty()) {
            list = audioService.getAudioListByStatus(status);
        } else {
            list = audioService.getAllAudioList();
        }
        return Result.success("获取成功", list);
    }
    
    /**
     * 审核通过音频
     */
    @PostMapping("/audio/{id}/approve")
    public Result<Void> approveAudio(@PathVariable Long id) {
        boolean success = audioService.approveAudio(id);
        return success ? Result.success("审核通过", null) : Result.error("审核失败");
    }
    
    /**
     * 审核拒绝音频
     */
    @PostMapping("/audio/{id}/reject")
    public Result<Void> rejectAudio(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String reason = body.get("reason");
        boolean success = audioService.rejectAudio(id, reason);
        return success ? Result.success("已拒绝", null) : Result.error("操作失败");
    }
}
