package com.weiju.springboot.controller;


import com.weiju.springboot.exception.BaseException;
import com.weiju.springboot.model.Credential;
import com.weiju.springboot.model.User;
import com.weiju.springboot.repository.CredentialRepository;
import com.weiju.springboot.repository.RoleRepository;
import com.weiju.springboot.repository.UserRepository;
import com.weiju.springboot.security.JwtTokenUtil;
import com.weiju.springboot.service.CredentialService;
import com.weiju.springboot.service.UserService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.authentication.AuthenticationManager;

import java.util.Map;
import java.util.Objects;


import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


@RestController
@RequestMapping("/auth/api")
public class AuthController {
    final private UserService userService;
    final private UserRepository userRepository;
    final private CredentialRepository credentialRepository;
    final private RoleRepository roleRepository;
    final protected UserDetailsService userDetailsService;
    private AuthenticationManager authenticationManager;
    private JwtTokenUtil jwtTokenUtil;
    private CredentialService credentialService;

    private static Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(UserService userService,
                          UserRepository userRepository,
                          @Qualifier("JwtUserDetailServiceImpl") UserDetailsService userDetailsService,
                          JwtTokenUtil jwtTokenUtil,
                          AuthenticationManager authenticationManager,
                          CredentialRepository credentialRepository,
                          RoleRepository roleRepository,
                          CredentialService credentialService
    ) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.credentialRepository = credentialRepository;
        this.roleRepository = roleRepository;
        this.credentialService = credentialService;
    }

    /**
     * Authenticates the user. If something is wrong, an {@link AuthenticationException} will be thrown
     */
    private void authenticate(String username, String password) throws Exception {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new DisabledException("User is disabled!", e);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Bad credentials!", e);
        }
    }

    @PostMapping(value = "/login")
    public ResponseEntity<String> login(@RequestBody Map<String, Map<String, Object>> payload) throws Exception {
        logger.info("user try to login" + '\n');
        JSONObject data = new JSONObject(payload.get("data"));
        logger.info(data.toString());
        String email = data.getString("email");
        String password = data.getString("password");
        if (userService.verifyLogin(email, password)) {
            logger.info("user info correct");
            User user = userRepository.findByEmail(email);
            logger.info("find user: " + email);
            JSONObject login_info = new JSONObject();
            login_info.put("user_id", user.getUserid());
            //TODO generate token
            logger.info("try to authenticate");
            authenticate(email, password);
            final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            logger.info("try to generate token");
            final String token = jwtTokenUtil.generateToken(userDetails);
            login_info.put("token", token);
            logger.info(token);
            JSONObject response = new JSONObject();
            response.put("data", login_info);
            return new ResponseEntity<>(response.toString(), HttpStatus.OK);
        } else {
            logger.warn("user not found");
            throw new BaseException("wrong password or email", HttpStatus.NOT_FOUND);
        }
    }

    // 用户注册
    @PostMapping(value = "/register")
    public ResponseEntity<String> userRegistration(@RequestBody Map<String, Map<String, Object>> payload) throws BaseException {
        logger.info("new user try to register");
        JSONObject data = new JSONObject(payload.get("data"));
        logger.info("get data:\n" + data.toString());
        //check email-exist:
        if (userRepository.existsByEmail(data.getString("email"))) {
            logger.warn("email already exist!");
            throw new BaseException("This email has already been registered","can not insert into users,violate email unique constrain",HttpStatus.BAD_REQUEST);
        }

        User user = userService.registerUser(data.getString("email"), data.getString("password"));
        // add credential
        credentialService.setRole(user, "USER_ANNOTATION_COLLECTION");
        credentialService.setRole(user, "USER_PUBLISHER");
        logger.info("set role done");

        logger.info("new user created: " + user.getEmail());
        JSONObject user_info = new JSONObject();
        user_info.put("email", user.getEmail());
        user_info.put("id", user.getUserid());
        user_info.put("password", user.getHashed_password());

        JSONObject return_info = new JSONObject();
        return_info.put("data", user_info);

        return new ResponseEntity<>(return_info.toString(), HttpStatus.CREATED);
    }
}
