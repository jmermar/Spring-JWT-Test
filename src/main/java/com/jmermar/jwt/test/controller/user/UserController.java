package com.jmermar.jwt.test.controller.user;

import com.jmermar.jwt.test.controller.user.payload.LoginUserRequest;
import com.jmermar.jwt.test.controller.user.payload.RegisterUserRequest;
import com.jmermar.jwt.test.controller.user.payload.UserLoggedResponse;
import com.jmermar.jwt.test.model.user.Role;
import com.jmermar.jwt.test.model.user.User;
import com.jmermar.jwt.test.model.user.UserRepository;
import com.jmermar.jwt.test.payload.ErrorMessage;
import com.jmermar.jwt.test.security.services.JwtService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
public class UserController {
    private PasswordEncoder encoder;
    private UserRepository userRepository;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;

    @GetMapping("/aboutme")
    public ResponseEntity<?> aboutMe(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginUserRequest loginUserRequest) {
        if (!userRepository.existsByEmail(loginUserRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new ErrorMessage("User " + "does not exists"));
        }

        final var user = userRepository.findByEmail(loginUserRequest.getEmail())
                .orElse(null);
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserRequest.getEmail(),
                        loginUserRequest.getPassword()));

        final var token = jwtService.generateJwtToken(authentication);

        return ResponseEntity.ok().body(new UserLoggedResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody RegisterUserRequest request) {
        final var user = User.builder().email(request.getEmail())
                .name(request.getName())
                .password(encoder.encode(request.getPassword())).role(Role.USER)
                .build();
        try {
            userRepository.save(user);
            return ResponseEntity.ok("User created successfully");
        } catch (RuntimeException ex) {
            log.error("Cannot register user: " + ex.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new ErrorMessage("Cannot create User"));
        }
    }
}
