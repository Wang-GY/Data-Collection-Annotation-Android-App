package com.weiju.springboot.controller;

import com.weiju.springboot.exception.BaseException;
import com.weiju.springboot.model.DataMetaErr;
import com.weiju.springboot.model.User;
import com.weiju.springboot.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.weiju.springboot.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository) {

        this.userService = userService;
        this.userRepository = userRepository;
    }

    // 用户注册
    @PostMapping(value = "/")

    public ResponseEntity<String> userRegistration(@RequestBody Map<String, Map<String, Object>> payload) throws Exception {

        JSONObject data = new JSONObject(payload.get("data"));
        System.out.println(data.toString());
        //check email-exist:
        if (!userRepository.existsByEmail(data.getString("email"))) {
            throw new BaseException("email already registered", HttpStatus.NOT_FOUND);
        }
        User user = userService.registerUser(data.getString("email"), data.getString("password"));

        JSONObject user_info = new JSONObject();
        user_info.put("email", user.getEmail());
        user_info.put("id", user.getUserid());
        user_info.put("password", user.getHashed_password());

        JSONObject return_info = new JSONObject();
        return_info.put("data", user_info);

        return new ResponseEntity<>(return_info.toString(), HttpStatus.CREATED);
    }

    //获取用户信息
    @GetMapping(value = "/{id}")
    public DataMetaErr getUserProfile(@PathVariable("id") int userid) {
        User user = userService.getUserProfile(userid);
        DataMetaErr response = new DataMetaErr();
        response.setData(user);
        return response;

    }

    //更新用户信息
    @PatchMapping(value = "/{id}")
    public DataMetaErr updateUserProfile(@PathVariable("id") int userid, @RequestBody DataMetaErr payload) throws Exception {
        Map<String, Object> user_data = (Map<String, Object>) payload.getData();
        User updated_user = userService.updateUser(user_data);
        DataMetaErr user_info = new DataMetaErr();
        user_info.setData(updated_user);
        return user_info;
    }
}
