package com.platform.main.shared;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecurityUtils {

    public static UUID getCurrentUserId() {
        return UUID.fromString(getPrincipal());
    }

    public static boolean isAdmin() {
        return getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public static boolean isAuthenticated() {
        return getAuthentication() != null
                && getAuthentication().isAuthenticated()
                && !getPrincipal().equals("anonymousUser");
    }

    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private static String getPrincipal() {
        return getAuthentication().getPrincipal().toString();
    }
}