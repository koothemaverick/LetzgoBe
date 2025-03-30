package com.letzgo.LetzgoBe.global.stomp;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompRabbitMqBrokerConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${spring.messaging.stomp.broker-relay.host}")
    private String host;
    @Value("${spring.messaging.stomp.broker-relay.port}")
    private int port;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS();  // CORS 허용
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry
                .setApplicationDestinationPrefixes("/app")
                .enableStompBrokerRelay("/topic")
                .setRelayHost(host)
                .setRelayPort(port)
                .setClientLogin("admin")
                .setClientPasscode("admin")
                .setSystemLogin("admin")
                .setSystemPasscode("admin");
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
