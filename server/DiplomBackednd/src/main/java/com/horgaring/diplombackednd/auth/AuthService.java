package com.horgaring.diplombackednd.auth;

import com.horgaring.diplombackednd.exception.DuplicateResourceException;
import com.horgaring.diplombackednd.exception.ResourceNotFoundException;
import com.horgaring.diplombackednd.mail.EmailService;
import com.horgaring.diplombackednd.security.JwtService;
import com.horgaring.diplombackednd.user.Gender;
import com.horgaring.diplombackednd.user.Role;
import com.horgaring.diplombackednd.user.User;
import com.horgaring.diplombackednd.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthResponse register(RegisterRequest request) {
        log.info("Registration attempt: email={}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .gender(request.getGender() != null ? Gender.valueOf(request.getGender()) : null)
                .role(Role.USER)
                .active(false)
                .build();

        userRepository.save(user);
        emailService.createAndSendMail(user.getEmail());
        log.info("User created: id={}, email={}, firstName={}", user.getId(), user.getEmail(), user.getFirstName());

        String token = jwtService.generateToken(user);
        log.info("Registration successful: id={}, email={}", user.getId(), user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt: email={}", request.getEmail());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getEmail()));

        String token = jwtService.generateToken(user);
        log.info("Login successful: id={}, email={}", user.getId(), user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .build();
    }
}
