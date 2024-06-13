package com.admin.reactive.dto.user;

import com.admin.reactive.entities.Role;
import com.admin.reactive.entities.User;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private String username;
    private String email;
    private List<String> roles;

    public static UserInfo from(User user) {
        return new UserInfo(
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream().map(Role::getName).toList()
        );
    }
}
