package com.fjdialect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 方言学习资源实体类
 * 对应数据库表：dialect_resource
 * 用途：存储方言学习资源（词汇、句子、发音等）
 * 关联：
 * - 被 DialectResourceController 提供 API 接口
 * - 被 DialectResourceService 处理业务逻辑
 * - 被前端 Learning.vue 展示学习
 */
@Data
@TableName("dialect_resource")
public class DialectResource {
    @TableId(type = IdType.AUTO)
    private Long id;              // 资源 ID
    
    private String title;         // 资源标题（如：福州话问候语）
    private String cityCode;      // 所属城市代码（fuzhou, xiamen 等）
    private String category;      // 分类：词汇、句子、发音、歌曲、故事
    private String content;       // 内容（文字内容，如方言对照）
    private String audioUrl;      // 音频链接（用于播放发音）
    private String imageUrl;      // 图片链接
    private String difficulty;    // 难度：初级、中级、高级
    private Integer viewCount;    // 浏览次数（统计热门资源）
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间
}
