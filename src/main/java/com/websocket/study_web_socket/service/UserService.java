package com.websocket.study_web_socket.service;

import com.websocket.study_web_socket.model.entity.User;

public interface UserService {
    User findById(String id);

    User findByUsername(String username);
}
