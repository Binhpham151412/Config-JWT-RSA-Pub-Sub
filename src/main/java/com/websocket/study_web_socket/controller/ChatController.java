package com.websocket.study_web_socket.controller;

import com.websocket.study_web_socket.exception.IllegalOperationException;
import com.websocket.study_web_socket.model.ApiResponse;
import com.websocket.study_web_socket.model.dto.ChatMessageDTO;
import com.websocket.study_web_socket.model.entity.ChatMessage;
import com.websocket.study_web_socket.model.entity.User;
import com.websocket.study_web_socket.repository.ChatMessageRepository;
import com.websocket.study_web_socket.service.FileService;
import com.websocket.study_web_socket.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatMessageRepository chatMessageRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserService userService;
    private final FileService fileService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> sendMessage(@RequestBody ChatMessageDTO chatMessageDTO, Authentication authentication) {
        try {
            String sender = authentication.getName();
            User receiver = userService.findById(chatMessageDTO.getReceiverId());

            if (chatMessageDTO.getContent() == null || chatMessageDTO.getContent().trim().isEmpty()) {
                throw new IllegalOperationException("Message content cannot be empty");
            }

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSender(sender);
            chatMessage.setReceiver(receiver.getUsername());
            chatMessage.setContent(chatMessageDTO.getContent());
            chatMessage.setTimestamp(LocalDateTime.now());
            chatMessageRepository.save(chatMessage);

            if (redisTemplate != null) {
                redisTemplate.convertAndSend("chat:" + receiver.getUsername(),
                    sender + ": " + chatMessageDTO.getContent());
            }

            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Message sent successfully"));
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage());
            throw e;
        }
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

    @PostMapping("/with-file")
    public ResponseEntity<ApiResponse<String>> sendMessageWithFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("receiverId") String receiverId,
            @RequestParam("content") String content,
            Authentication authentication) throws FileUploadException {
        try {
            String sender = authentication.getName();
            User receiver = userService.findById(receiverId);

            String fileUrl = fileService.uploadFile(file);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSender(sender);
            chatMessage.setReceiver(receiver.getUsername());
            chatMessage.setContent(content);
            chatMessage.setFileUrl(fileUrl);
            chatMessage.setFileType(determineFileType(file.getContentType()));
            chatMessage.setTimestamp(LocalDateTime.now());

            chatMessageRepository.save(chatMessage);

            if (redisTemplate != null) {
                redisTemplate.convertAndSend("chat:" + receiver.getUsername(),
                    String.format("%s sent a file: %s", sender, fileUrl));
            }

            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Message with file sent successfully"));
        } catch (Exception e) {
            log.error("Error sending message with file: {}", e.getMessage());
            throw e;
        }
    }

    private String determineFileType(String contentType) {
        if (contentType.startsWith("image/")) return "IMAGE";
        if (contentType.startsWith("video/")) return "VIDEO";
        return "DOCUMENT";
    }
}