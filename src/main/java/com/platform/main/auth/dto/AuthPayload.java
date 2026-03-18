package com.platform.main.auth.dto;

import com.platform.main.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthPayload {
    private String token;
    private String refreshToken;
    private User user;
}