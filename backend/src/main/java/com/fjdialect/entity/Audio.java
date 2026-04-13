package com.fjdialect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 音频实体类
 * 对应数据库表：audio
 * 用途：存储用户上传的方言音频
 */
@Data
@TableName("audio")
public class Audio {
    @TableId(type = IdType.AUTO)
    private Long id;              // 音频 ID
    
    private String title;         // 音频标题
    private String cityCode;      // 所属城市代码
    private String dialectType;   // 方言类型
    private String description;   // 音频描述
    private String audioUrl;      // 音频文件 URL
    private String uploader;      // 上传者用户名
    private Long uploaderId;      // 上传者用户 ID
    private Integer rating;       // 点赞数
    private String status;        // 状态：pending(待审核), approved(已通过), rejected(已拒绝)
    private String rejectReason;  // 拒绝原因
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间
}
