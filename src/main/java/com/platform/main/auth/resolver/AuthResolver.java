package com.platform.main.auth.resolver;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.platform.main.auth.dto.AuthPayload;
import com.platform.main.auth.dto.LoginInput;
import com.platform.main.auth.dto.RegisterInput;
import com.platform.main.auth.entity.User;
import com.platform.main.auth.repository.UserRepository;
import com.platform.main.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

@DgsComponent
@RequiredArgsConstructor
public class AuthResolver {

    private final AuthService authService;
    private final UserRepository userRepository;

    @DgsMutation
    public AuthPayload register(@InputArgument RegisterInput input) {
        return authService.register(input);
    }

    @DgsMutation
    public AuthPayload login(@InputArgument LoginInput input) {
        return authService.login(input);
    }

    @DgsQuery
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