package com.fjdialect.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fjdialect.entity.User;
import com.fjdialect.mapper.UserMapper;
import com.fjdialect.service.UserService;
import com.fjdialect.util.PasswordEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void register(User user) {
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        String encodedPassword = PasswordEncoderUtil.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userMapper.insert(user);
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectList(new LambdaQueryWrapper<User>().orderByDesc(User::getCreateTime));
    }

    @Override
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        User user = userMapper.selectById(id);
        if (user != null) {
            String encodedPassword = PasswordEncoderUtil.encode(newPassword);
            user.setPassword(encodedPassword);
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }

    @Override
    public void updateUserInfo(User user) {
        User existingUser = userMapper.selectById(user.getId());
        if (existingUser != null) {
            // 只更新允许修改的字段
            existingUser.setUsername(user.getUsername());
            existingUser.setPhone(user.getPhone());
            existingUser.setEmail(user.getEmail());
            existingUser.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(existingUser);
        }
    }
}
