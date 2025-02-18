package com.websocket.study_web_socket.controller;

import com.websocket.study_web_socket.config.JwtTokenProvider;
import com.websocket.study_web_socket.model.ApiResponse;
import com.websocket.study_web_socket.model.dto.JwtAuthenticationResponse;
import com.websocket.study_web_socket.model.dto.LoginRequest;
import com.websocket.study_web_socket.model.entity.User;
import com.websocket.study_web_socket.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), new JwtAuthenticationResponse(jwt)));
    }

    @PostMapping(value = "/save")
    public ResponseEntity<ApiResponse<String>> saveUser(@RequestBody LoginRequest loginRequest) {
        String encodedPassword = passwordEncoder.encode(loginRequest.getPassword());

        User user = new User();
        user.setUuid(UUID.randomUUID().toString());
        user.setUsername(loginRequest.getUsername());
        user.setPassword(encodedPassword);

        authService.saveUser(user);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "save " + user.getUsername() + " success"));
    }
}
