package com.websocket.study_web_socket.service;

import com.websocket.study_web_socket.model.entity.User;
import com.websocket.study_web_socket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public void saveUser(User user) {
        userRepository.save(user);
    }
}
