package com.weiju.springboot.controller;

import com.weiju.springboot.model.User;
import com.weiju.springboot.repository.UserRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.weiju.springboot.service.UserService;

import javax.management.ObjectName;
import javax.xml.soap.SAAJResult;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    // 用户注册
    @PostMapping(value = "/")
    public ResponseEntity<String> userRegistration(@RequestBody Map<String, Map<String, Object>> payload) {
        logger.info("new user try to register");
        JSONObject data = new JSONObject(payload.get("data"));
        logger.info("get data:\n" + data.toString());
        //check email-exist:
        if (!userRepository.existsByEmail(data.getString("email"))) {
            logger.warn("email already exist!");
            // throw new BaseException("email already registered", HttpStatus.NOT_FOUND);
        }

        User user = userService.registerUser(data.getString("email"), data.getString("password"));
        logger.info("new user created: " + user.getEmail());
        JSONObject user_info = new JSONObject();
        user_info.put("email", user.getEmail());
        user_info.put("id", user.getUserid());
        user_info.put("password", user.getHashed_password());

        JSONObject return_info = new JSONObject();
        return_info.put("data", user_info);

        return new ResponseEntity<>(return_info.toString(), HttpStatus.CREATED);
    }

    //获取用户信息
    @GetMapping(value = "/{id}/")
    public ResponseEntity<Object> getUserProfile(@PathVariable("id") int userid) {
        logger.info("get user info : " + String.valueOf(userid));
        User user = userService.getUserProfile(userid);

        Map<String,Object> res = new LinkedHashMap<>();
        res.put("data", user);
        logger.info("construct response");
        return new ResponseEntity<>(res, HttpStatus.OK);

    }

    //更新用户信息
    @PatchMapping(value = "/{id}/")
    public ResponseEntity<Object> updateUserProfile(@PathVariable("id") int userid, @RequestBody Map<String, Object> payload) throws Exception {
        Map<String, Object> user_data = (Map<String, Object>) payload.get("data");
        User updated_user = userService.updateUser(user_data);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("data", updated_user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
