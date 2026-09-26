package com.codingshuttle.youtube.hospitalManagement.controller;

import com.codingshuttle.youtube.hospitalManagement.dto.LoginRequestDto;
import com.codingshuttle.youtube.hospitalManagement.dto.LoginResponseDto;
import com.codingshuttle.youtube.hospitalManagement.dto.SignupRequestDto;
import com.codingshuttle.youtube.hospitalManagement.dto.SignupResponseDto;
import com.codingshuttle.youtube.hospitalManagement.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

   private final AuthService authService;

   @PostMapping("/signup")
   public ResponseEntity<SignupResponseDto> signup(@RequestBody SignupRequestDto signupRequestDto){
       return ResponseEntity.ok(authService.signup(signupRequestDto));
   }


   @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
       return ResponseEntity.ok(authService.login(loginRequestDto));
   }
}
