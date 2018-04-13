package com.weiju.springboot.service.impl;

import com.weiju.springboot.model.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.weiju.springboot.repository.UserRepository;
import com.weiju.springboot.service.UserService;

import javax.validation.constraints.Null;
import java.util.Iterator;
import java.util.Map;

@Service("User Service")
public class UserServiceImpl implements UserService {

    final
    UserRepository userRepository;

    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public UserServiceImpl(UserRepository userRepository,JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
    }


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

    @Override
    public User updateUser(Map<String,Object> user_info) {
        int id = (Integer) user_info.get("userid");
        User user = userRepository.findByUserid(id);
        //TODO NO such user
        if (user==null)
                return null;
        for (String key : user_info.keySet()) {
            Object value = user_info.get(key);
            if (key.equals("userid"))
                continue;
            switch (key) {
                case "email":
                    user.setEmail((String) user_info.get(key));
                    break;
                case "gender":
                    user.setGender((Integer) user_info.get(key));
                    break;
                case "phone":
                    user.setAvatar((String) user_info.get(key));
                    break;
                case "avatar":
                    user.setAvatar((String) user_info.get(key));
                    break;
                case "nick_name":
                    user.setNike_name((String) user_info.get(key));
                    break;

            }

        }
        userRepository.save(user);
        return user;

    }

    @Override
    public Boolean verifyLogin(String email, String password) {
        User user = userRepository.findByEmail(email);
        //TODO generate error
        if (user== null){
            return false;
        }
        else if (user.getHashed_password().equals(hashPassword(password))){
            return true;
        }
        return false;
    }
    //TODO encrypt password
    private String hashPassword(String password){
        return password;

    }
}
