package com.weiju.springboot.repository;

import com.weiju.springboot.model.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Integer> {
    Role findByRole(String role);
}
