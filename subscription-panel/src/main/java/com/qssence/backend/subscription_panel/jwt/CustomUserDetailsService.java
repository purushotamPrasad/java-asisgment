package com.qssence.backend.subscription_panel.jwt;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Hardcoded user for testing
        if ("admin".equals(username)) {
            return User.builder()
                    .username("admin")
                    .password(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("admin@123"))
                    .roles("ADMIN") // Role of the user
                    .build();
        } else {
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }
}
