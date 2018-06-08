package com.weiju.springboot.service.impl;

import com.weiju.springboot.model.Credential;
import com.weiju.springboot.model.Role;
import com.weiju.springboot.model.User;
import com.weiju.springboot.repository.CredentialRepository;
import com.weiju.springboot.repository.RoleRepository;
import com.weiju.springboot.service.CredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("CredentialServiceImpl")
public class CredentialServiceImpl implements CredentialService {
    final
    CredentialRepository credentialRepository;
    final RoleRepository roleRepository;

    @Autowired
    public CredentialServiceImpl(CredentialRepository credentialRepository,
                                 RoleRepository roleRepository) {
        this.credentialRepository = credentialRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void setRole(User user, Role role) {
        Credential credential = new Credential();
        credential.setUser(user);
        credential.setRole(role);
        credentialRepository.save(credential);
    }

    @Override
    public void setRole(User user, String role) {
        Role role1 = roleRepository.findByRole(role);
        setRole(user, role1);
    }

}
