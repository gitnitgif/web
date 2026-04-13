package com.fjdialect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论实体类
 * 对应数据库表：comment
 * 用途：存储用户评论，用于交流互动功能
 * 关联：
 * - 被 CommentController 提供 API 接口
 * - 被 CommentService 处理业务逻辑
 * - 被前端 Community.vue 展示评论
 * - 支持回复功能（通过 parentId 关联父评论）
 */
@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;              // 评论 ID
    
    private Long userId;          // 用户 ID（关联 user 表）
    private String username;      // 用户名（冗余存储，避免联表查询）
    private String content;       // 评论内容
    
    private String resourceType;  // 资源类型：dialect（学习资源）、folk（文化资源）、city（城市）
    private Long resourceId;      // 资源 ID（关联对应资源表）
    
    private Long parentId;        // 父评论 ID（用于回复，null 表示主评论）
    private Integer likeCount;    // 点赞数
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间
}
