package com.weiju.springboot.service.impl;

import com.weiju.springboot.controller.AuthController;
import com.weiju.springboot.exception.BaseException;
import com.weiju.springboot.model.Credential;
import com.weiju.springboot.model.Role;
import com.weiju.springboot.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.weiju.springboot.repository.UserRepository;
import com.weiju.springboot.service.UserService;

import javax.jws.soap.SOAPBinding;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service("User Service")
public class UserServiceImpl implements UserService {

    private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    final
    UserRepository userRepository;

    private final JdbcTemplate jdbcTemplate;

    private final String DEFAULT_AVATAR = "http://ww1.sinaimg.cn/large/006lTfYGgy1fsb881kvpjj307v07vwec.jpg";

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
    }


    @Override
    public User getUserProfile(int user_id) {
        return userRepository.findByUserid(user_id);
    }

    @Override
    public User registerUser(String email, String password) {

        User user = new User();
        user.setEmail(email);
        user.setHashed_password(hashPassword(password));
        user.setAvatar(DEFAULT_AVATAR);
        userRepository.save(user);
        return user;

    }

    @Override
    public List<Role> getRules(User user) {
        List<Credential> credentials = user.getCredentials();
        List<Role> roles = new LinkedList<>();
        for (Credential credential : credentials) {
            logger.debug("add credential with role:" + credential.getRole().getRole());
            roles.add(credential.getRole());
        }
        return roles;
    }

    // TODO update password
    @Override
    public User updateUser(Map<String, Object> user_info) throws BaseException {

        int id = (Integer) user_info.get("user_id");
        User user = userRepository.findByUserid(id);
        //TODO NO such user
        if (user == null)
            throw new BaseException("user not found", String.format("can not find user by this id: %d", id), HttpStatus.NOT_FOUND);
        for (String key : user_info.keySet()) {
            Object value = user_info.get(key);
            if (key.equals("user_id"))
                continue;
            switch (key) {
                case "email":
                    user.setEmail((String) user_info.get(key));
                    break;
                case "gender":
                    user.setGender((Integer) user_info.get(key));
                    break;
                case "phone":
                    user.setPhone((String) user_info.get(key));
                    break;
                case "avatar":
                    user.setAvatar((String) user_info.get(key));
                    break;
                case "username":
                    user.setUsername((String) user_info.get(key));
                    break;
                // allow extra information. do nothing
//                default:
//                    throw new BaseException("update fail", String.format("can not update this field: %s, you are not allowed or key error", key), HttpStatus.BAD_REQUEST);

            }

        }
        userRepository.save(user);
        return user;

    }

    @Override
    public Boolean verifyLogin(String email, String password) throws BaseException {
        if (!userRepository.existsByEmail(email)) {
            throw new BaseException("wrong password or email", "email not registered", HttpStatus.NOT_FOUND);
        }
        User user = userRepository.findByEmail(email);
        //TODO generate error
        logger.info("raw password: " + password);
        logger.info("database password: " + user.getHashed_password());
        logger.info("hashed password: " + hashPassword(password));
        if (new BCryptPasswordEncoder().matches(password, user.getHashed_password())) {
            return true;
        } else {
            throw new BaseException("wrong password or email", "wrong password", HttpStatus.BAD_REQUEST);
        }
    }

    //TODO encrypt password
    private String hashPassword(String password) {

        return new BCryptPasswordEncoder().encode(password);
    }
}
