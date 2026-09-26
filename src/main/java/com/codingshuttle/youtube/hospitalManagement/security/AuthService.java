package com.codingshuttle.youtube.hospitalManagement.security;

import com.codingshuttle.youtube.hospitalManagement.dto.LoginRequestDto;
import com.codingshuttle.youtube.hospitalManagement.dto.LoginResponseDto;
import com.codingshuttle.youtube.hospitalManagement.dto.SignupRequestDto;
import com.codingshuttle.youtube.hospitalManagement.dto.SignupResponseDto;
import com.codingshuttle.youtube.hospitalManagement.entity.Patient;
import com.codingshuttle.youtube.hospitalManagement.entity.User;
import com.codingshuttle.youtube.hospitalManagement.entity.type.AuthProviderType;
import com.codingshuttle.youtube.hospitalManagement.entity.type.RoleType;
import com.codingshuttle.youtube.hospitalManagement.repository.PatientRepository;
import com.codingshuttle.youtube.hospitalManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientRepository patientRepository;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
        );

        User user = (User) authentication.getPrincipal();

        String token = authUtil.generatedAccessToken(user);

        return new LoginResponseDto(token, user.getId());
    }

    @Transactional
    public User signUpInternal(SignupRequestDto signupRequestDto, AuthProviderType authProviderType, String providerId){
        User user = userRepository.findByUsername(signupRequestDto.getUsername()).orElse(null);

        if (user != null) throw new IllegalArgumentException("User Already Exist...");

        Set<RoleType> roles = signupRequestDto.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = Set.of(RoleType.PATIENT);
        }

        user = User.builder()
                .username(signupRequestDto.getUsername())
                .providerId(providerId)
                .providerType(authProviderType)
                .roles(roles)
                .build();

        if (authProviderType == AuthProviderType.EMAIL){
            user.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
        }

        user = userRepository.save(user);

        Patient patient = Patient.builder()
                .name(signupRequestDto.getName() != null ? signupRequestDto.getName() : signupRequestDto.getUsername())
                .email(signupRequestDto.getUsername())
                .user(user)
                .build();
        patientRepository.save(patient);

        return user;
    }

    // signup controller
    @Transactional
    public SignupResponseDto signup(SignupRequestDto signupRequestDto) {
        User user = signUpInternal(signupRequestDto, AuthProviderType.EMAIL, null);
        return new SignupResponseDto(user.getId(), user.getUsername());
    }

    @Transactional
    public ResponseEntity<LoginResponseDto> handleOAuthLoginRequest(OAuth2User oAuth2User, String registrationId) {
        // fetch providerType and providerId
        AuthProviderType providerType = authUtil.getProviderTypeFromRegistrationId(registrationId);
        String providerId = authUtil.determineProviderIdFromOAuth2User(oAuth2User, registrationId);

        // save the providerType and provider id info with user
        User user = userRepository.findByProviderIdAndProviderType(providerId, providerType).orElse(null);
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User emailUser = (email != null && !email.isBlank()) ? userRepository.findByUsername(email).orElse(null) : null;

        if (user == null && emailUser == null){
            // signup flow:-
            String username = authUtil.determineUsernameFromOAuth2User(oAuth2User, registrationId, providerId);
            user = signUpInternal(new SignupRequestDto(username, null, name, Set.of(RoleType.PATIENT)), providerType, providerId);
        } else if (user != null){
            if (email != null && !email.isBlank() && !email.equalsIgnoreCase(user.getUsername())){
                if (emailUser == null) {
                    user.setUsername(email);
                    user = userRepository.save(user);
                }
            }
        } else {
             throw new BadCredentialsException("This email is already registered with provider " + emailUser.getProviderType());
        }
        // if the user has an account: directly login
        // otherwise, first signup and then login

        LoginResponseDto loginResponseDto = new LoginResponseDto(authUtil.generatedAccessToken(user), user.getId());

        return ResponseEntity.ok(loginResponseDto);
    }
}
