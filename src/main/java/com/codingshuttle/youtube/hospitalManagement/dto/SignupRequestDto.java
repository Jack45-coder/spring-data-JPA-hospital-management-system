package com.codingshuttle.youtube.hospitalManagement.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class SignupRequestDto {
    private String username;
    private String password;
}
