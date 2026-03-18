package com.platform.main.auth.resolver;

import com.platform.main.auth.dto.AuthPayload;
import com.platform.main.auth.dto.LoginInput;
import com.platform.main.auth.dto.RegisterInput;
import com.platform.main.auth.entity.User;
import com.platform.main.auth.repository.UserRepository;
import com.platform.main.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AuthResolver {

    private final AuthService authService;
    private final UserRepository userRepository;

    @MutationMapping
    public AuthPayload register(@Argument RegisterInput input) {
        return authService.register(input);
    }

    @MutationMapping
    public AuthPayload login(@Argument LoginInput input) {
        return authService.login(input);
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public User me() {
        // JwtAuthenticationFilter already validated the token
        // and stored the userId as the principal — just read it
        String userId = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();

        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}