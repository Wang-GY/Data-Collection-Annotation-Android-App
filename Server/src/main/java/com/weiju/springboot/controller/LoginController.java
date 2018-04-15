package com.weiju.springboot.controller;


import com.weiju.springboot.model.User;
import com.weiju.springboot.repository.UserRepository;
import com.weiju.springboot.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
public class LoginController {
    final private UserService userService;
    final private UserRepository userRepository;

    @Autowired
    public LoginController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping(value = "")
    public String login(@RequestBody Map<String, Map<String, Object>> payloda) {
        JSONObject data = new JSONObject(payloda.get("data"));
        String email = data.getString("email");

        if (userService.verifyLogin(email, data.getString("password"))) {
            User user = userRepository.findByEmail(email);
            System.out.println(user.getUserid());
            JSONObject login_info = new JSONObject();
            login_info.put("user_id",user.getUserid());
            //TODO generate token
            login_info.put("token","THIS IS TOKEN");
            JSONObject response = new JSONObject();
            response.put("data",login_info);
            //JSONObject 不能被自动解析为json
            return response.toString();
        } else {
            // TODO generate error
            System.out.println("NO such user");
            return null;
        }
    }
}
