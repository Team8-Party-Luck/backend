package com.partyluck.party_luck.websocket.service;


import com.partyluck.party_luck.domain.User;
import com.partyluck.party_luck.repository.UserRepository;
import com.partyluck.party_luck.security.jwt.JwtDecoder;
import com.partyluck.party_luck.websocket.domain.ChatMessage;
import com.partyluck.party_luck.websocket.domain.ChatRoom;
import com.partyluck.party_luck.websocket.dto.MessageRequestDto;
import com.partyluck.party_luck.websocket.dto.MessageResponseDto;
import com.partyluck.party_luck.websocket.repository.ChatMessageRepository;
import com.partyluck.party_luck.websocket.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final JwtDecoder jwtDecoder;

    public MessageResponseDto save(MessageRequestDto message, String token) {
        String username = jwtDecoder.decodeUsername(token);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다.")
        );
        Long userId = user.getId();
        String chatRoomId = message.getRoomId();
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId).orElseThrow(
                () -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다.")
        );
        String msg = message.getMessage();
        ChatMessage.MessageType messageType = message.getType();
        String createdAt = message.getCreatedAt();

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .senderId(userId)
                .message(message)
                .build();
        chatMessageRepository.save(chatMessage);

        return MessageResponseDto.builder()
                .nickname(user.getNickname())
                .message(message.getMessage())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
