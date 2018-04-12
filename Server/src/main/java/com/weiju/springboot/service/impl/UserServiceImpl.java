package service.impl;

import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.UserRepository;
import service.UserService;

@Service("User Service")
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Override
    public User getUserProfile(int user_id){
        return userRepository.findByUserid(user_id);
    }

    @Override
    public User saveUser(User user) {
        return null;
    }
}
