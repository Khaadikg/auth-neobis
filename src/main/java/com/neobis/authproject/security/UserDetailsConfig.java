package com.neobis.authproject.security;

import com.neobis.authproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsConfig implements UserDetailsService {
    private final UserRepository repository;

    @Autowired
    public UserDetailsConfig(UserRepository repository) {
        this.repository = repository;
    }

    private final static String USER_NOT_FOUND_MSG = "user with username %s not found";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException(
                        String.format(USER_NOT_FOUND_MSG, username))
        );
    }
}
