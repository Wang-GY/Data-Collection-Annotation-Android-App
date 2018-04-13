package com.weiju.springboot.service;


import com.weiju.springboot.model.User;
import org.json.JSONObject;

import java.util.Map;

public interface UserService {

    User getUserProfile(int user_id);

    User registerUser(String email, String password);

    User updateUser(Map<String,Object> user);

    Boolean verifyLogin(String email, String hashed_password);
}
