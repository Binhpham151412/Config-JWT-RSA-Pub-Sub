package com.websocket.study_web_socket.controller;

import com.websocket.study_web_socket.model.ApiResponse;
import com.websocket.study_web_socket.model.dto.ChatMessageDTO;
import com.websocket.study_web_socket.model.entity.ChatMessage;
import com.websocket.study_web_socket.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> sendMessage(@RequestBody ChatMessageDTO chatMessageDTO, Authentication authentication) {

        String sender = authentication.getName();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(sender);
        chatMessage.setReceiver(chatMessageDTO.getReceiver());
        chatMessage.setContent(chatMessageDTO.getContent());
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(chatMessage);

        // (Tùy chọn) Publish tin nhắn lên Redis để thông báo cho người nhận
        if (redisTemplate != null) {
            redisTemplate.convertAndSend("chat:" + chatMessageDTO.getReceiver(), sender + ": " + chatMessageDTO.getContent());
        }
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Send message success"));
    }

    @GetMapping(value = "/history")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@RequestParam String withUser, Authentication authentication) {
        String currentUser = authentication.getName();
        List<ChatMessage> messages = chatMessageRepository.findBySenderAndReceiverOrSenderAndReceiverOrderByTimestampAsc(
                currentUser,
                withUser,
                withUser,
                currentUser
        );
        return ResponseEntity.ok(messages);
    }
}
