package com.vbz.dbcards.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // Main websocket endpoint
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();  // Required for React Native / SockJS clients
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // Broker for sending messages to clients
        registry.enableSimpleBroker("/topic", "/queue");

        // Prefix for messages from client to server
        registry.setApplicationDestinationPrefixes("/app");

        // Required for user specific notifications
        registry.setUserDestinationPrefix("/user");
    }
}