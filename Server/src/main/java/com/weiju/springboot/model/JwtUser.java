package com.weiju.springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;

/*
* https://juejin.im/post/58c29e0b1b69e6006bce02f4
* */
public class JwtUser implements UserDetails {

    private final int id;
    private final String username;
    private final String password;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;
    // TODO private final Date lastPasswordResetDate;

    public JwtUser(int id,
                   String username,
                   String password,
                   String email,
                   // Date lastPasswordResetDate,
                   Collection<? extends GrantedAuthority> authorities
    ) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.authorities = authorities;
        // this.lastPasswordResetDate = lastPasswordResetDate;

    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    /*
        @JsonIgnore
        public Date getLastPasswordResetDate() {
            return lastPasswordResetDate;
        }
    */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    /**
     * 使用email 作为username
     *
     * @return
     */
    @Override
    public String getUsername() {
        return email;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //TODO block a user if necessary
    @Override
    public boolean isEnabled() {
        return true;
    }
}
