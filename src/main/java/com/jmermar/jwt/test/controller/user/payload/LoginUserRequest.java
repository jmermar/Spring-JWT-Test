package com.jmermar.jwt.test.controller.user.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginUserRequest {
    String email;
    String password;
}
