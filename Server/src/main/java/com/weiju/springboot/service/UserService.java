package com.weiju.springboot.service;


import com.weiju.springboot.model.User;

public interface UserService {

    User getUserProfile(int user_id);

    User registerUser(String email, String password);

    User updateUser(User user);

    Boolean verifyLogin(String email, String hashed_password);
}
