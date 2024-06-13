package com.admin.reactive.service;

import com.admin.reactive.dto.user.UserInfo;
import com.admin.reactive.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    void create(User user);
    UserInfo findById(Long id);
    Optional<User> findByUsername(String username);
    List<UserInfo> findAll();
}
