package com.admin.reactive.service;

import com.admin.reactive.dto.user.UserInfo;
import com.admin.reactive.entities.User;
import com.admin.reactive.repository.RoleRepository;
import com.admin.reactive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void create(User user) {
        if (findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username " + user.getUsername() + "already exist");
        }

        user.setRoles(Set.of(roleRepository.findByName("ROLE_USER").get()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public UserInfo findById(Long id) {
        return UserInfo.from(userRepository.findById(id).orElseThrow(() ->
                new UsernameNotFoundException("Username with id " + id + " not found")));
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("Username " + username + "not found"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(it -> new SimpleGrantedAuthority(it.getName()))
                        .toList())
                .build();
    }

    @Override
    @Transactional
    public List<UserInfo> findAll() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(UserInfo::from)
                .toList();
    }
}
