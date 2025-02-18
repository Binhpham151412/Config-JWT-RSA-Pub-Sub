package com.websocket.study_web_socket.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@Getter
@Slf4j
public class JwtKeyProvider {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtKeyProvider() throws Exception {
        this.privateKey = loadPrivateKey();
        this.publicKey = loadPublicKey();
        log.info("Keys loaded successfully!");
    }

    // Load Private Key từ file .pem
    private PrivateKey loadPrivateKey() throws Exception {
        try {
            byte[] keyBytes = readKeyFile("private_key.pem");
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        } catch (Exception e) {
            log.error("Error loading private key: {}", e.getMessage());
            throw e;
        }
    }

    // Load Public Key từ file .pem
    private PublicKey loadPublicKey() throws Exception {
        try {
            byte[] keyBytes = readKeyFile("public_key.pem");
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
        } catch (Exception e) {
            log.error("Error loading public key: {}", e.getMessage());
            throw e;
        }
    }

    // Đọc file từ classpath
    private byte[] readKeyFile(String filename) throws IOException {
        ClassPathResource resource = new ClassPathResource(filename);
        try (InputStream inputStream = resource.getInputStream()) {
            String key = new String(inputStream.readAllBytes());
            log.info("Successfully read file: {}", filename);
            key = key.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            return Base64.getDecoder().decode(key);
        } catch (IOException e) {
            log.error("Error reading file: {}. Ensure it is in resources.", filename, e);
            throw e;
        }
    }
}