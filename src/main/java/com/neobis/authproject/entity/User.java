package com.neobis.authproject.entity;

import com.neobis.authproject.entity.enums.Role;
import com.neobis.authproject.entity.enums.UserState;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity @Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Enumerated(EnumType.STRING)
    Role role;
    @Column(unique = true)
    String username;
    @Enumerated(EnumType.STRING)
    UserState state;
    @Column(name = "uuid")
    String UUID;
    @Column(name = "uuid_creation_date")
    LocalDateTime UUIDExpirationDate;
    @Column(unique = true)
    String email;
    String password;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        return  authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
