package com.websocket.study_web_socket.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Slf4j
public class SecurityConfig {

    /**
     * Creates a bean for the JwtAuthenticationFilter.
     * This filter validates JWT tokens in incoming requests.
     *
     * @return an instance of JwtAuthenticationFilter.
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        log.info("Creating JwtAuthenticationFilter bean...");
        return new JwtAuthenticationFilter();
    }

    /**
     * Configures the Security Filter Chain, setting up HTTP security rules.
     * - Disables CSRF protection.
     * - Configures endpoints under '/api/auth/**' to be accessible without authentication. <br>
     * - Requires authentication for all other endpoints.
     * - Sets session management to STATELESS.
     * - Adds the JwtAuthenticationFilter before UsernamePasswordAuthenticationFilter.
     *
     * @param http the HttpSecurity configuration object.
     * @return the configured SecurityFilterChain.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring SecurityFilterChain...");
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection for APIs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // Allow public access to authentication endpoints
                        .anyRequest().authenticated() // Require authentication for all other endpoints
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Enforce stateless sessions

        // Add the JwtAuthenticationFilter to validate tokens in incoming requests
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        log.info("SecurityFilterChain configured successfully.");
        return http.build();
    }

    /**
     * Creates a bean for the AuthenticationManager.
     *
     * @param authConfig the AuthenticationConfiguration object.
     * @return an instance of AuthenticationManager.
     * @throws Exception if an error occurs while getting the AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        log.info("Creating AuthenticationManager bean...");
        return authConfig.getAuthenticationManager();
    }

    /**
     * Creates a bean for the PasswordEncoder.
     * Uses BCrypt for password encoding.
     *
     * @return an instance of BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("Creating PasswordEncoder bean (BCrypt)...");
        return new BCryptPasswordEncoder();
    }
}