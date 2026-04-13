package com.fjdialect.controller;

import com.fjdialect.entity.User;
import com.fjdialect.service.UserService;
import com.fjdialect.util.PasswordEncoderUtil;
import com.fjdialect.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<User> login(@RequestBody User user) {
        User existingUser = userService.getUserByUsername(user.getUsername());
        if (existingUser == null || !PasswordEncoderUtil.matches(user.getPassword(), existingUser.getPassword())) {
            return Result.error(401, "用户名或密码错误");
        }
        existingUser.setPassword("");
        return Result.success("登录成功", existingUser);
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        User existingUser = userService.getUserByUsername(user.getUsername());
        if (existingUser != null) {
            return Result.error(400, "用户名已存在");
        }
        userService.register(user);
        return Result.success("注册成功");
    }

    @PutMapping("/update")
    public Result<User> updateUserInfo(@RequestBody User user) {
        userService.updateUserInfo(user);
        User updatedUser = userService.getUserByUsername(user.getUsername());
        if (updatedUser != null) {
            updatedUser.setPassword("");
            return Result.success("更新成功", updatedUser);
        }
        return Result.error(400, "更新失败");
    }
}
