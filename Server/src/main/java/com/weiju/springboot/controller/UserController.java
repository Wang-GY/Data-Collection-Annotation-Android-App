package com.weiju.springboot.controller;

import com.weiju.springboot.model.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.weiju.springboot.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserService userService;

    // 用户注册
    @PostMapping(value = "/")
    public String userRegistration(@RequestBody Map<String,Map<String,Object>> payload){

        JSONObject data = new JSONObject(payload.get("data"));
        System.out.println(data.toString());

        User user = userService.registerUser(data.getString("email"),data.getString("password"));

        JSONObject user_info = new JSONObject();
        user_info.put("email",user.getEmail());
        user_info.put("id",user.getUserid());
        user_info.put("password",user.getHashed_password());

        JSONObject return_info = new JSONObject();
        return_info.put("data",user_info);

        return return_info.toString();
    }

    //获取用户信息
    @GetMapping(value = "/{id}")
    public String getUserProfile(@PathVariable("id") int userid){
      User user=  userService.getUserProfile(userid);
      JSONObject user_info = new JSONObject();
      user_info.put("data",new JSONObject(user));
      return user_info.toString();

    }
}
