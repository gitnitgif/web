package com.fjdialect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

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
