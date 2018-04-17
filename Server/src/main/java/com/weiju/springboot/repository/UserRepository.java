package com.weiju.springboot.repository;

import com.weiju.springboot.model.User;
import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository<User, Integer> {
    User findByUserid(int userid);

    User findByEmail(String email);

    boolean existsByEmail(String email);


}
