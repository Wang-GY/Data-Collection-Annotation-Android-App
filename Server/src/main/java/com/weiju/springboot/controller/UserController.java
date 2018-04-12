package controller;

import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping(value = "/")
    public String userRegistration(@RequestBody Map<String,Object> payload){
        System.out.println("payload");
        return "Hi";
    }
    @GetMapping(value = "/{id}")
    public User getUserProfile(@PathVariable("id") int userid){
      return   userService.getUserProfile(userid);

    }
}
