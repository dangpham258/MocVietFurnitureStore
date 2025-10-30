package mocviet.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple broker cho destinations prefix /topic và /queue
        // /topic: broadcast messages (cho tất cả subscribers)
        // /queue: point-to-point messages (cho user cụ thể)
        config.enableSimpleBroker("/topic", "/queue");
        
        // Prefix cho client gửi message đến server
        config.setApplicationDestinationPrefixes("/app");
        
        // Prefix cho user destinations (dùng với @SendToUser)
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đăng ký WebSocket endpoint, client sẽ kết nối đến đây
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Cho phép tất cả origins (có thể hạn chế sau)
                .withSockJS(); // Hỗ trợ SockJS fallback cho browsers không hỗ trợ WebSocket
    }
}
