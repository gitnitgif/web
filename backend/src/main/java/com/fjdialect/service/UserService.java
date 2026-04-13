package com.fjdialect.service;

import com.fjdialect.entity.User;
import java.util.List;

public interface UserService {
    void register(User user);
    User getUserByUsername(String username);
    List<User> getAllUsers();
    void deleteUser(Long id);
    void resetPassword(Long id, String newPassword);
    void updateUserInfo(User user);
}
