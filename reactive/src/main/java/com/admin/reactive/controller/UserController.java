package com.admin.reactive.controller;

import com.admin.reactive.dto.user.UserInfo;
import com.admin.reactive.entities.User;
import com.admin.reactive.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/name")
    public String getName(Authentication authentication) {
        return ResponseEntity.ok(authentication.getName()).getBody();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping
    public UserInfo getNameById(@RequestParam("id") String id) {
        return ResponseEntity.ok(userService.findById(Long.valueOf(id))).getBody();
    }

    @PreAuthorize(("hasAnyAuthority('ROLE_ADMIN')"))
    @GetMapping("/list")
    public List<UserInfo> getAllUsers() {
        return ResponseEntity.ok(userService.findAll()).getBody();
    }
}
