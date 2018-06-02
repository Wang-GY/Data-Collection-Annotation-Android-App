package com.weiju.springboot.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "roles")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "role_id")
    private int roleId;
    @Column(name = "role")
    private String role;

    public String getRole() {
        return role;
    }

    public Role() {
    }

    public Role(String name) {
        this.role = name;
    }

}
