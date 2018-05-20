package com.weiju.springboot.controller;


import com.weiju.springboot.exception.BaseException;
import com.weiju.springboot.model.User;
import com.weiju.springboot.repository.UserRepository;
import com.weiju.springboot.service.UserService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    public LoginController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping(value = "")
    public ResponseEntity<String> login(@RequestBody Map<String, Map<String, Object>> payload) throws BaseException {
        logger.info("user try to login" + '\n');
        JSONObject data = new JSONObject(payload.get("data"));
        logger.info(data.toString());
        String email = data.getString("email");
        String password = data.getString("password");
        if (userService.verifyLogin(email, password)) {
            User user = userRepository.findByEmail(email);
            logger.info("find user: " + email);
            JSONObject login_info = new JSONObject();
            login_info.put("user_id", user.getUserid());
            //TODO generate token
            login_info.put("token", "THIS IS TOKEN");
            JSONObject response = new JSONObject();
            response.put("data", login_info);
            return new ResponseEntity<>(response.toString(), HttpStatus.OK);
        } else {
            logger.warn("user not found");
            throw new BaseException("wrong password or email", HttpStatus.NOT_FOUND);
        }
    }
}
