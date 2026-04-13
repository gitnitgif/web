package com.fjdialect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库表：user
 * 用途：存储系统用户信息，包括管理员和普通用户
 * 关联：
 * - 被 AuthController 用于登录注册
 * - 被 AdminController 用于用户管理
 * - 被 UserService 处理业务逻辑
 */
@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)  // 主键，自增
    private Long id;               // 用户 ID
    private String username;       // 用户名（登录账号）
    private String password;       // 密码（BCrypt 加密）
    private String phone;          // 手机号
    private String email;          // 邮箱
    private String role;           // 角色：ADMIN（管理员）/ USER（普通用户）
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间
}
