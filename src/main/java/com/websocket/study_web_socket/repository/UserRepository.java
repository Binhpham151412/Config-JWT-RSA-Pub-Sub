package com.websocket.study_web_socket.repository;

import com.websocket.study_web_socket.model.ApiResponse;
import com.websocket.study_web_socket.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
}
