package com.websocket.study_web_socket.repository;

import com.websocket.study_web_socket.model.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findBySenderAndReceiverOrSenderAndReceiverOrderByTimestampAsc(
            String sender1,
            String receiver1,
            String sender2,
            String receiver2
    );
}
