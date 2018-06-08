package com.weiju.springboot.service;

import com.weiju.springboot.controller.UserController;
import com.weiju.springboot.model.JwtUser;
import com.weiju.springboot.model.Role;
import com.weiju.springboot.model.User;
import com.weiju.springboot.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service("JwtUserDetailServiceImpl")
@Primary
public class JwtUserDetailServiceImpl implements UserDetailsService {
    private static Logger logger = LoggerFactory.getLogger(JwtUserDetailServiceImpl.class);
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public JwtUserDetailServiceImpl(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;

    }

    /**
     * use email, not username
     *
     * @param email
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("called loadUserByUsername");
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with email '%s'.", email));
        } else {
            logger.info("user found");
            List<Role> roles = userService.getRules(user);
            logger.debug("user roles found");
            if (roles == null) {
                logger.debug("user has no roles");
            } else {
                logger.debug("user with roles:");
                logger.info(String.valueOf(roles.size()));
                for (Role role : roles) {
                    logger.info(role.getRole());
                }
            }
            JwtUser jwtUser = new JwtUser(
                    user.getUserid(),
                    user.getUsername(),
                    user.getHashed_password(),
                    user.getEmail(),
                    mapToGrantedAuthorities(roles)
                    // TODO set last password reset date
            );
            logger.info("generate user details done");
            if (jwtUser == null) {
                logger.info("jwtuser is null");
            } else {
                logger.info("jwtuser is not null");
                logger.debug(jwtUser.getUsername());
            }

            return jwtUser;
        }

    }

    private Collection<? extends GrantedAuthority> mapToGrantedAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());
    }
}
