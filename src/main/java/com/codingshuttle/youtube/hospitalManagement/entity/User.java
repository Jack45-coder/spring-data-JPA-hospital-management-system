package com.codingshuttle.youtube.hospitalManagement.entity;

import com.codingshuttle.youtube.hospitalManagement.entity.type.AuthProviderType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "app_user", indexes = {
        @Index(name = "idx_provider_id, provider_type", columnList = "providerId, providerType")
})
public class User implements UserDetails {

    public static User builder;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(unique = true)
    private String username;
    private String password;

    private String providerId;

    @Enumerated(EnumType.STRING)
    private AuthProviderType providerType;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
