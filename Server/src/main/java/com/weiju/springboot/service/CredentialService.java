package com.weiju.springboot.service;

import com.weiju.springboot.model.Role;
import com.weiju.springboot.model.User;

public interface CredentialService {
    void setRole(User user, Role role);

    void setRole(User user, String role);
}
