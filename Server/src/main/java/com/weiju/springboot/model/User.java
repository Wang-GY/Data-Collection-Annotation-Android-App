package com.weiju.springboot.model;

import javax.persistence.*;

@Entity
@Table(name="users")
public class User {
    @Id
    @Column(name = "userid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userid;

    @Column(name="email")
    private String email;

    @Column(name = "hashed_password")
    private String hashed_password;

    @Column(name = "nock_name")
    private String nake_name;

    @Column(name = "gender")
    private int gender;

    @Column(name = "register_date")
    private String register_date;

    @Column(name="level")
    private int level;

    @Column(name="phone")
    private String phone;

    @Column(name="avatar")
    private String avatar; //url for avatar

    @Column(name = "privilege")
    private int privilege;

    @Column(name = "balance")
    private int balance;

}
