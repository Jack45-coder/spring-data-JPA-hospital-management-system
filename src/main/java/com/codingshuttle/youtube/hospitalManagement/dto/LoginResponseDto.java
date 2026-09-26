package com.codingshuttle.youtube.hospitalManagement.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    String jwt;
    Long userId;
}
