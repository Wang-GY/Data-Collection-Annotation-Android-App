package com.weiju.springboot.service;


import com.weiju.springboot.model.Role;
import com.weiju.springboot.model.User;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public interface UserService {

    User getUserProfile(int user_id);

    User registerUser(String email, String password);

    List<Role> getRules(User user);

    User updateUser(Map<String,Object> user);

    Boolean verifyLogin(String email, String hashed_password);
}
