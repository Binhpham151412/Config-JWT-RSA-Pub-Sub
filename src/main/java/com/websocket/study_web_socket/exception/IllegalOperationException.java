package com.websocket.study_web_socket.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalOperationException extends RuntimeException {
    public IllegalOperationException(String message) {
        super(message);
    }
}
