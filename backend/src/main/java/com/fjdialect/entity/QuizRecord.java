package com.fjdialect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户测验记录实体类
 * 对应数据库表：quiz_record
 * 用途：存储用户在线测试的记录（成绩）
 * 关联：
 * - 被 QuizController 保存答题记录
 * - 被 QuizService 处理业务逻辑
 * - 被前端 Quiz.vue 展示历史成绩
 * - 通过 userId 关联 user 表
 */
@Data
@TableName("quiz_record")
public class QuizRecord {
    @TableId(type = IdType.AUTO)
    private Long id;              // 记录 ID
    
    private Long userId;          // 用户 ID（关联 user 表）
    private String username;      // 用户名（冗余存储）
    private Integer totalQuestions;  // 总题数
    private Integer correctCount;    // 正确题数
    private Integer score;           // 得分（百分制）
    private String cityCode;         // 测试城市代码
    private LocalDateTime createTime;  // 创建时间（测试时间）
}
