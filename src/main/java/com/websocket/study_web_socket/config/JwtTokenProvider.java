package com.websocket.study_web_socket.config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {
//    /** config jwt HMAC-SHA (secretKey auto generate) */
//    private final Key key;
//
//    public JwtTokenProvider() {
//        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//    }
//
//    public String generateToken(Authentication authentication) {
//        String username = authentication.getName();
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + 300000);
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(now)
//                .setExpiration(expiryDate)
//                .signWith(key)
//                .compact();
//    }
//
//    public String getUsernameFromJwt(String token) {
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//        return claims.getSubject();
//    }
//
//    public boolean validateToken(String authToken) {
//        try {
//            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
//            return true;
//        } catch (JwtException exception) {
//            log.error("JWT Token invalid {}", authToken);
//        }
//        return false;
//    }

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtTokenProvider(PrivateKey privateKey, PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    /**
     * Generates a JWT token using the private key.
     *
     * @param authentication User authentication details.
     * @return A signed JWT string.
     */
    public String generateToken(Authentication authentication) {
        log.info("Generating JWT for user: {}", authentication.getName());
        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    /**
     * Validates the given token using the public key.
     *
     * @param token The JWT token to validate.
     * @return True if valid, false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            log.info("Validating JWT token...");

            Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException exception) {
            log.error("Invalid JWT token: {}", token, exception);
            return false;
        }
    }

    /**
     * Extracts the username from the JWT.
     *
     * @param token The JWT token.
     * @return The username contained in the token.
     */
    public String getUserNameFromToken(String token) {
        log.info("Extracting username from JWT...");
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
