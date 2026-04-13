package com.fjdialect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测验题目实体类
 * 对应数据库表：quiz
 * 用途：存储在线测试的题目（单选题）
 * 关联：
 * - 被 QuizController 提供 API 接口
 * - 被 QuizService 处理业务逻辑
 * - 被前端 Quiz.vue 用于在线测试
 * - 被 QuizRecord 关联（记录答题情况）
 */
@Data
@TableName("quiz")
public class Quiz {
    @TableId(type = IdType.AUTO)
    private Long id;              // 题目 ID
    
    private String title;         // 题目标题（如：福州话问候语测试）
    private String cityCode;      // 所属城市代码（fuzhou, xiamen 等）
    private String category;      // 分类：词汇、句子、发音、文化
    private String question;      // 问题内容
    private String optionA;       // 选项 A
    private String optionB;       // 选项 B
    private String optionC;       // 选项 C
    private String optionD;       // 选项 D
    private String answer;        // 正确答案（A/B/C/D）
    private String explanation;   // 答案解析（答题后显示）
    private Integer difficulty;   // 难度（1-5 星）
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间
}
