package com.weiju.springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {


    @Id
    @Column(name = "userid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int userid;

    @Column(name = "email")
    private String email;

    @Column(name = "hashed_password")
    private String hashed_password;

    @Column(name = "username")
    private String username;

    @Column(name = "gender")
    private int gender;

    @Column(name = "register_date")
    private String register_date;

    @Column(name = "level")
    private int level;

    @Column(name = "phone")
    private String phone;

    @Column(name = "avatar")
    private String avatar; //url for avatar

    @Column(name = "privilege")
    private int privilege;

    @Column(name = "balance")
    private int balance;


    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = Task.class)
    @JsonIgnore
    private List<Task> tasks; // user tasks

    //mappedBy : model 层定义的变量，不是数据库的字段
    @OneToMany(mappedBy = "committer", cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = Commit.class)
    @JsonIgnore
    private List<Commit> commits;


    public List<Commit> getCommits() {
        return commits;
    }

    public List<Task> getTasks() {
        return tasks;
    }


    public int getUserid() {
        return userid;
    }

    public String getEmail() {
        return email;
    }

    public String getHashed_password() {
        return hashed_password;
    }

    public String getUsername() {
        return username;
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

    public void setUsername(String username) {
        this.username = username;
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
