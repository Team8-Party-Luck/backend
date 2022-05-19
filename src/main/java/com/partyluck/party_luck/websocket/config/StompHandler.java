package com.partyluck.party_luck.websocket.config;

import com.partyluck.party_luck.security.provider.JWTAuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
// import ... 생략

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JWTAuthProvider jwtAuthProvider;

    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
            boolean isTrue = jwtAuthProvider.validateToken(accessor.getFirstNativeHeader("token"));
            if(!isTrue) {
                throw new IllegalArgumentException("해당 토큰이 유효하지 않습니다.");
            }
        }
        return message;
    }
}