package com.weiju.springboot.repository;

import com.weiju.springboot.model.Credential;
import org.springframework.data.repository.CrudRepository;

public interface CredentialRepository extends CrudRepository<Credential, Integer> {

}
