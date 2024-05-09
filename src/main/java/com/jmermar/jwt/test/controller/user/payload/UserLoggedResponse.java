package com.jmermar.jwt.test.controller.user.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserLoggedResponse {
    String token;
}
