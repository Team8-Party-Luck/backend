package com.partyluck.party_luck.websocket.controller;


import com.partyluck.party_luck.domain.User;
import com.partyluck.party_luck.repository.UserRepository;
import com.partyluck.party_luck.security.UserDetailsImpl;
import com.partyluck.party_luck.security.jwt.JwtDecoder;

import com.partyluck.party_luck.websocket.dto.reponse.EQMessageDto;
import com.partyluck.party_luck.websocket.dto.reponse.MessageResponseDto;
import com.partyluck.party_luck.websocket.dto.request.MessageRequestDto;
import com.partyluck.party_luck.websocket.repository.AlarmRepository;
import com.partyluck.party_luck.websocket.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 해당 컨트롤러는 Message Handler라고 생각하면 됨
@RequiredArgsConstructor
@RestController
public class ChatMessageController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final JwtDecoder jwtDecoder;
    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;


    // 메시지 목록 불러오기
    @GetMapping("/chatroom/get/{chatroomId}")
    public List<MessageResponseDto> readMessages(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable String chatroomId) {
        return chatMessageService.readMessages(userDetails, chatroomId);
    }

    // stomp-ws를 통해 해당 경로로 메시지가 들어왔을 때 메시지의 "destination header"와 "messageMapping"에
    // 설정된 경로가 일치하는 handler를 찾고 처리한다
    // "confinguration"에서 설정한 "app"이라는 "prefix"와 합쳐져 "app/hello"라는 "destination header"를 가진
    // 메시지들이 이 handler를 타게 된다.
    // 1. 처음 채팅방에 들어왔을 때 호출되는 메시지
    @MessageMapping("/enter")
    public void entering(MessageRequestDto message, @Header("token") String token) {
        String username = jwtDecoder.decodeUsername(token);
        User foundUser = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.")
        );
        String nickname = foundUser.getNickname();
        String enterMessage = nickname + "님이 입장하셨습니다.";
//        messagingTemplate.convertAndSend("/queue/" + message.getChatRoomId(), enterMessage);
        EQMessageDto eqMessageDto=new EQMessageDto(enterMessage,message.getChatRoomId());
        String topic=channelTopic.getTopic();
        redisTemplate.convertAndSend(topic,enterMessage);
    }

    // 2. 채팅방을 나갔을 때 호출되는 메시지
    @MessageMapping("/quit")
    public void quiting(MessageRequestDto message, @Header("token") String token) {
        String username = jwtDecoder.decodeUsername(token);
        User foundUser = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.")
        );
        String nickname = foundUser.getNickname();
        // 방을 나가는 로직을 처리한다.
        // ------------------------------------------------

        ///////////////////////////////////////////////////////
        String quitMessage = nickname + "님이 퇴장하셨습니다.";
//        messagingTemplate.convertAndSend("/queue/" + message.getChatRoomId(), quitMessage);
        EQMessageDto eqMessageDto=new EQMessageDto(quitMessage,message.getChatRoomId());
        String topic=channelTopic.getTopic();
        redisTemplate.convertAndSend(topic,eqMessageDto);
    }

    // 3. 채팅 메시지 처리하기
    @MessageMapping("/send")
    public void sending(MessageRequestDto message, @Header("token") String token) {
        System.out.println("메시지 전송 확인");
        System.out.println(message);
        System.out.println(message.getMessage());
        System.out.println(message.getChatRoomId());
        MessageResponseDto messageResponseDto = chatMessageService.save(message, token);
        System.out.println("메시지 전송 확인");
//        messagingTemplate.convertAndSend("/queue/" + message.getChatRoomId(), messageResponseDto);
        String topic=channelTopic.getTopic();
        redisTemplate.convertAndSend(topic,messageResponseDto);
    }

}