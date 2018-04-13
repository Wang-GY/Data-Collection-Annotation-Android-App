package com.weiju.springboot.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;

@Entity
@Table(name="users")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    @Id
    @Column(name = "userid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userid;

    @Column(name="email")
    private String email;

    @Column(name = "hashed_password")
    private String hashed_password;

    @Column(name = "nick_name")
    private String nike_name;

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


    public int getUserid() {
        return userid;
    }

    public String getEmail() {
        return email;
    }

    public String getHashed_password() {
        return hashed_password;
    }

    public String getNike_name() {
        return nike_name;
    }

    public int getGender() {
        return gender;
    }

    public String getRegister_date() {
        return register_date;
    }

    public int getLevel() {
        return level;
    }

    public String getPhone() {
        return phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getPrivilege() {
        return privilege;
    }

    public int getBalance() {
        return balance;
    }



    public void setUserid(int userid) {
        this.userid = userid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setHashed_password(String hashed_password) {
        this.hashed_password = hashed_password;
    }

    public void setNike_name(String nike_name) {
        this.nike_name = nike_name;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setRegister_date(String register_date) {
        this.register_date = register_date;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setPrivilege(int privilege) {
        this.privilege = privilege;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

}
