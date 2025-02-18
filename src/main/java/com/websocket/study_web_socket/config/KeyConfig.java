package com.websocket.study_web_socket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.PrivateKey;
import java.security.PublicKey;

@Configuration
@Slf4j
public class KeyConfig {

    private final JwtKeyProvider jwtKeyProvider;

    public KeyConfig(JwtKeyProvider jwtKeyProvider) {
        this.jwtKeyProvider = jwtKeyProvider;
    }

    @Bean
    public PrivateKey privateKey() {
        log.info("Key config private key {}", jwtKeyProvider.getPrivateKey());
        return jwtKeyProvider.getPrivateKey(); // Trả về private key từ JwtKeyProvider
    }

    @Bean
    public PublicKey publicKey() {
        log.info("Key config public key {}", jwtKeyProvider.getPublicKey());
        return jwtKeyProvider.getPublicKey(); // Trả về public key từ JwtKeyProvider
    }
}