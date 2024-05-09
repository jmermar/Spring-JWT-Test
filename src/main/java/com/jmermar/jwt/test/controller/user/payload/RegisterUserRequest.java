package com.jmermar.jwt.test.controller.user.payload;

import lombok.Data;

@Data
public class RegisterUserRequest {
    private String email;
    private String name;
    private String password;
}
