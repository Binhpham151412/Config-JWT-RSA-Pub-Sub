package com.websocket.study_web_socket.service.impl;

import com.websocket.study_web_socket.exception.IllegalOperationException;
import com.websocket.study_web_socket.model.entity.User;
import com.websocket.study_web_socket.repository.UserRepository;
import com.websocket.study_web_socket.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalOperationException("User not found"));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalOperationException("User not found"));
    }
}
