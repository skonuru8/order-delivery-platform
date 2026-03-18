package com.platform.main.auth.service;

import com.platform.main.auth.dto.AuthPayload;
import com.platform.main.auth.dto.LoginInput;
import com.platform.main.auth.dto.RegisterInput;
import com.platform.main.auth.entity.User;
import com.platform.main.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthPayload register(RegisterInput registerInput) {

        // 1. Reject Duplicate Emails
        if(userRepository.existsByEmail(registerInput.getEmail())) {
            throw new RuntimeException("Email Already Exists");
        }

        //2. Build user
        User user = new User();
        user.setEmail(registerInput.getEmail());
        user.setName(registerInput.getName());
        user.setPasswordHash(passwordEncoder.encode(registerInput.getPassword()));
        user.setRole("CUSTOMER");

        User savedUser = userRepository.save(user);

        //3. Issue tokens
        String token = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return new AuthPayload(token, refreshToken, savedUser);
    }

    public AuthPayload login(LoginInput loginInput) {

        //1. Find user
        User user = userRepository.findByEmail(loginInput.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        //2. verify pwd hash
        if(!passwordEncoder.matches(loginInput.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        //3. issue fresh tokens
        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthPayload(token, refreshToken, user);
    }
}
