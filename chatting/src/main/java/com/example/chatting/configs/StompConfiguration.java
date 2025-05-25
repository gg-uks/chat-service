package com.example.chatting.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@EnableWebSocketMessageBroker  //STOMP 기능을 활성화 하려면 이거를 미리 설정해야 한다 인에이블로
@Configuration
public class StompConfiguration implements WebSocketMessageBrokerConfigurer { //스톰프도 WebSocketMessageBrokerConfigurer 인터페이스를 구현해줌으로 스톰프기능을 쓸수있다

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) { //웹소켓 클라이언트가 어떤 경로로 서버로 접근해야하는지 지정해주는 것이다
        registry.addEndpoint("/stomp/chats"); //이결로로 통해서 서버와 커넥션이 가능하다
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) { // 클라이언트에서 메시지를 발행하고 클라이언트는 브로커로부터 전달되는 메시지 를 받기위해 구독을 신청해야하는데 그경로를 지정해주는것이다
        registry.setApplicationDestinationPrefixes("/pub"); // 메시지를 발행할수 있게 설정 경로  //클라이언트가 메시지를 보낼 때 사용하는 경로.  예를 들어 클라이언트가 /pub/chat/message로 메시지를 보내면, 서버에서 @MessageMapping("/chat/message")과 매핑된 메서드가 실행
        registry.enableSimpleBroker("/sub");//메시지구독을 신청할수 있도록 설정  //클라이언트가 구독하는 경로.클라이언트가 /sub/chat/room/1234로 구독하면, 서버는 그 경로로 메시지를 전송할 수 있음
    }
}
