package com.partyluck.party_luck.websocket.service;

import com.partyluck.party_luck.domain.User;
import com.partyluck.party_luck.repository.InitialInfoRepository;
import com.partyluck.party_luck.repository.UserRepository;
import com.partyluck.party_luck.security.UserDetailsImpl;
import com.partyluck.party_luck.security.jwt.JwtDecoder;
import com.partyluck.party_luck.websocket.domain.ChatMessage;
import com.partyluck.party_luck.websocket.domain.ChatRoom;
import com.partyluck.party_luck.websocket.domain.JoinChatRoom;
import com.partyluck.party_luck.websocket.dto.reponse.MessageResponseDto;
import com.partyluck.party_luck.websocket.dto.request.MessageRequestDto;
import com.partyluck.party_luck.websocket.repository.ChatMessageRepository;
import com.partyluck.party_luck.websocket.repository.ChatRoomRepository;
import com.partyluck.party_luck.websocket.repository.JoinChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final JoinChatRoomRepository joinChatRoomRepository;
    private final JwtDecoder jwtDecoder;
    private final InitialInfoRepository initialInfoRepository;

    public MessageResponseDto save(MessageRequestDto message, String token) {
        System.out.println("save 시작!");
        String username = jwtDecoder.decodeUsername(token);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다.")
        );

        Long userId = user.getId();
        String chatRoomId = message.getChatRoomId();
        System.out.println(message.getChatRoomId());
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
                .message(message.getMessage())
                .createdAt(message.getCreatedAt())
                .userId(chatMessage.getSenderId())
                .imageUrl(initialInfoRepository.findInitialInfoByUserId(userId).orElse(null).getProfile_img())
                .build();
    }

    // 해당 채팅방 메시지 조회
    public List<MessageResponseDto> readMessages(UserDetailsImpl userDetails, String chatroomId) {
        Long userId = userDetails.getUser().getId();

        // 해당방에 맞는 유저인지 검증
        List<JoinChatRoom> joinChatRoomList = joinChatRoomRepository.findJoinChatRoomsByChatRoom_ChatRoomId(chatroomId);
        int cnt=0;
        for(JoinChatRoom joinChatRoom : joinChatRoomList) {
            if(joinChatRoom.getUser().getId().equals(userId)) {
                break;
            }
            cnt++;
        }
        if(cnt==joinChatRoomList.size())
            throw new IllegalArgumentException("해당 채팅방에 잘못된 유저가 접근하였습니다.");
        List<ChatMessage> chatMessageList = chatMessageRepository.findChatMessagesByChatroom_ChatRoomIdOrderByCreatedAt(chatroomId);
        System.out.println("채팅 메시지 개수 : " + chatMessageList);

        List<MessageResponseDto> messageResponseDtoList = new ArrayList<>();

        for(ChatMessage chatMessage : chatMessageList) {
            Long senderId = chatMessage.getSenderId();
            User sender = userRepository.findById(senderId).orElseThrow(
                    () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.")
            );

            MessageResponseDto messageResponseDto = MessageResponseDto.builder()
                    .message(chatMessage.getMessage())
                    .createdAt(chatMessage.getCreatedAt().toString())
                    .userId(senderId)
                    .imageUrl(initialInfoRepository.findInitialInfoByUserId(senderId).orElse(null).getProfile_img())
                    .build();
            messageResponseDtoList.add(messageResponseDto);
        }
        return messageResponseDtoList;
    }

}
