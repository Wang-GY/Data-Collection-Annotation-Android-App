package service;


import model.User;

public interface UserService {

    User getUserProfile(int user_id);

    User saveUser(User user);
}
