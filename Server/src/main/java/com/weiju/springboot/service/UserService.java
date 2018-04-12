package com.weiju.springboot.service;


import com.weiju.springboot.model.User;

public interface UserService {

    User getUserProfile(int user_id);

    User saveUser(User user);
}
