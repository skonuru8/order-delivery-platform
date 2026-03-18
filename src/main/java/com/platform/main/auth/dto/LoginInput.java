package com.platform.main.auth.dto;

import lombok.Data;

@Data
public class LoginInput {
    private String email;
    private String password;
}