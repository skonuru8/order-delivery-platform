package com.platform.main.auth.dto;

import lombok.Data;

@Data
public class RegisterInput {
    private String name;
    private String email;
    private String password;
}