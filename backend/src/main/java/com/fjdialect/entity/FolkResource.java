package com.fjdialect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 方言文化资源实体类（歌曲、童谣、故事等）
 * 对应数据库表：folk_resource
 * 用途：存储方言文化资源（歌曲、童谣、故事、谚语）
 * 关联：
 * - 被 FolkResourceController 提供 API 接口
 * - 被 FolkResourceService 处理业务逻辑
 * - 被前端 FolkResource.vue 展示文化资源
 */
@Data
@TableName("folk_resource")
public class FolkResource {
    @TableId(type = IdType.AUTO)
    private Long id;              // 资源 ID
    
    private String title;         // 资源标题（如：爱拼才会赢）
    private String cityCode;      // 所属城市代码（fuzhou, xiamen 等）
    private String type;          // 类型：歌曲、童谣、故事、谚语
    private String content;       // 内容（歌词、故事文本等）
    private String translation;   // 普通话翻译/注释
    private String audioUrl;      // 音频链接（用于播放歌曲/故事）
    private String imageUrl;      // 图片链接
    private String author;        // 作者/演唱者
    private String description;   // 描述/背景介绍
    private Integer viewCount;    // 浏览次数
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间
}
