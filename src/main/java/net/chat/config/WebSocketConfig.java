package net.chat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.chat.websocket.WebSocketChatHandler;
import net.chat.websocket.WebSocketMemberInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@Log4j2
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final WebSocketChatHandler websocketHandler;
    private final WebSocketMemberInterceptor websocketMemberInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(websocketHandler, "/ws/chat")
                .addInterceptors(websocketMemberInterceptor)
                .setAllowedOrigins("*");
    }
}
