package com.example.chatting.configs;

import com.example.chatting.handlers.WebSocketChatHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@RequiredArgsConstructor
@EnableWebSocket //웹소켓을 사용할것이다라는 어노테이션이다
@Configuration //설정파일
public class WebSocketConfiguration implements WebSocketConfigurer {

    final WebSocketChatHandler webSocketChatHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // ws/chats로 들어오는 WebSocket 요청을 WebSocketChatHandler가 처리함.
        registry.addHandler(webSocketChatHandler , "/ws/chats"); //webSocketChatHandler 를 등록을 해줘야 한다  "/ws/chats" 이라는 경로를 지정해줄수 있다 서버에 사용할수 있게
    }
}
