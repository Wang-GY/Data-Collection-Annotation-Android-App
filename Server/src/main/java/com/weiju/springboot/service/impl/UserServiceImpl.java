package com.weiju.springboot.service.impl;

import com.weiju.springboot.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.weiju.springboot.repository.UserRepository;
import com.weiju.springboot.service.UserService;

@Service("User Service")
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Override
    public User getUserProfile(int user_id){
        return userRepository.findByUserid(user_id);
    }

    @Override
    public User registerUser(String email, String password) {

        User user = new User();
        user.setEmail(email);
        user.setHashed_password(hashPassword(password));

        userRepository.save(user);
        return user;

    }

    public String hashPassword(String password){
        return password;

    }
}
