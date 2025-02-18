package com.websocket.study_web_socket.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat_message", schema = "chat")
public class ChatMessage {

    @Id
    @UuidGenerator
    private UUID id;

    private String sender;
    private String receiver;
    private String content;
    private LocalDateTime timestamp;
}
