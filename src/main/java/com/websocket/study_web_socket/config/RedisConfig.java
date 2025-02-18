package com.websocket.study_web_socket.config;

import com.websocket.study_web_socket.pubsub.RedisMessageSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@Slf4j
public class RedisConfig {

    /**
     * Defines a Redis topic that will be used for communication via pub-sub.
     *
     * @return ChannelTopic representing the Redis topic (name: "chat").
     */
    @Bean
    public ChannelTopic topic() {
        log.info("Creating a Redis topic for pub-sub communication...");
        return new ChannelTopic("chat");
    }

    /**
     * Builds a RedisMessageListenerContainer to listen to published messages on the Redis topic.
     *
     * @param connectionFactory Redis connection factory for establishing the connection with Redis.
     * @param subscriber        The subscriber that will process the messages published to the topic.
     * @param topic             The Redis topic to listen to.
     * @return RedisMessageListenerContainer configured to listen to the given topic.
     */
    @Bean
    public RedisMessageListenerContainer redisContainer(
            RedisConnectionFactory connectionFactory,
            RedisMessageSubscriber subscriber,
            ChannelTopic topic
    ) {
        log.info("Setting up the Redis Message Listener Container...");
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();

        try {
            container.setConnectionFactory(connectionFactory); // Connects to the Redis server
            container.addMessageListener(subscriber, topic); // Subscribes to listen for messages on the topic
            log.info("Redis Message Listener Container setup successfully.");
        } catch (Exception e) {
            log.error("Error setting up Redis Message Listener: {}", e.getMessage(), e);
        }

        return container;
    }

    /**
     * Creates a RedisTemplate to perform key-value operations on Redis storage.
     *
     * @param connectionFactory Redis connection factory.
     * @return RedisTemplate<String, String> for key-value Redis operations.
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        log.info("Creating a Redis Template for key-value operations...");
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory); // Connects the RedisTemplate to Redis
        return template;
    }
}