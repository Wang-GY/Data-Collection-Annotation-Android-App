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

    //TODO check id
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
