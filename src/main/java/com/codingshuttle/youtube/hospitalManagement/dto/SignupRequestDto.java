package com.codingshuttle.youtube.hospitalManagement.dto;

import com.codingshuttle.youtube.hospitalManagement.entity.type.RoleType;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class SignupRequestDto {
    private String username;
    private String password;
    private String name;

    private Set<RoleType> roles = new HashSet<>();
}
