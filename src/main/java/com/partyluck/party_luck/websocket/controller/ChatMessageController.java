package com.partyluck.party_luck.websocket.controller;


import com.partyluck.party_luck.domain.User;
import com.partyluck.party_luck.repository.UserRepository;
import com.partyluck.party_luck.security.UserDetailsImpl;
import com.partyluck.party_luck.security.jwt.JwtDecoder;
import com.partyluck.party_luck.websocket.dto.reponse.MessageResponseDto;
import com.partyluck.party_luck.websocket.dto.request.MessageRequestDto;
import com.partyluck.party_luck.websocket.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

// 해당 컨트롤러는 Message Handler라고 생각하면 됨
@RequiredArgsConstructor
@Controller
public class ChatMessageController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final JwtDecoder jwtDecoder;
    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;


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
    public void entering(@RequestBody MessageRequestDto message, @Header("token") String token) {
        String username = jwtDecoder.decodeUsername(token);
        User foundUser = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.")
        );
        String nickname = foundUser.getNickname();
        String enterMessage = nickname + "님이 입장하셨습니다.";
        messagingTemplate.convertAndSend("/queue/" + message.getRoomId(),enterMessage);
    }

    // 2. 채팅방을 나갔을 때 호출되는 메시지
    @MessageMapping("/quit")
    public void quiting(@RequestBody MessageRequestDto message, @Header("token") String token) {
        String username = jwtDecoder.decodeUsername(token);
        User foundUser = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.")
        );
        String nickname = foundUser.getNickname();
        // 방을 나가는 로직을 처리한다.
        // ------------------------------------------------

        ///////////////////////////////////////////////////////
        String quitMessage = nickname + "님이 퇴장하셨습니다.";
        messagingTemplate.convertAndSend("/queue/" + message.getRoomId(),quitMessage);
    }

    // 3. 채팅 메시지 처리하기
    @MessageMapping("/send")
    public void sending(@RequestBody MessageRequestDto message, @Header("token") String token) {
        MessageResponseDto messageResponseDto = chatMessageService.save(message ,token);
        messagingTemplate.convertAndSend("/queue/" + message.getRoomId(),messageResponseDto);
    }



//    // stomp ws를 통해 해당 경로로 메세지가 들어왔을때 메시지의 "destination header"와 "messageMapping"에
//    // 설정된 경로가 일치하는 "handler"를 찾고 처리
//    // "configuration"에서 설정한 "app"이라는 "prifix"과 합쳐서 "app/hello"라는 "destination header"를 가진
//    // 메세지들이 이 handler를 타게 된다.
//    @MessageMapping("/hello")
//    // handler에서 처리를한 반환값을 "/topic/greetings" 경로로 다시 반환
//    // 앞에 "/topic"이 붙었으니 "simpleBroker"로 전달
//    // @SendTo("/topic/greetings")
//    public void greeting(@RequestBody MessageRequestDto message, @Header("token") String token) {
//        String username = jwtDecoder.decodeUsername(token);
//        String nickname = userRepository.findByUsername(username).get().getNickname();
//        System.out.println("chatHandler 에서 roomId : " + message.getRoomId());
//        System.out.println("chatHandler 에서 message : " + message.getMessage());
//        System.out.println("chatHandler 에서 nickname : " + nickname);
//        System.out.println("chatHandler 에서 type : " + message.getType());
//        // 로그인 회원 정보를 들어온 메시지에 값 세팅
//        // String username = jwtDecoder.decodeUsername(token);
//
//        // 방입장 메세지 처리
//        ChatMessage chatMessage = chatMessageService.saveMessage(message);
//
//        List<MessageResponseDto> chatMessageList = new ArrayList<>();
//
//        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
//            System.out.println("====================enter 메세지가 들어왔습니다.================================");
//
//            /** Todo
//             *  1. 입장메세지
//             *  2. [해당 유저가 chatRoomJoin에 존재하지 않을경우 ]
//             *     -입장 시간 저장
//             *  3. [해당 유저가 chatRoomJoin에 존재 할 경우]
//             *     -해당 유저의 입장시간 이후의 메세지를 select 해서 list로 전달
//             */
//            if (chatRoomJoinService.userEnterChk(message).isPresent()) {
//                System.out.println("enter 메세지가 들어왔을때--------방 입장 정보가 있을때 실행합니다.-----------------------------------");
//                // 채팅방 내용
//                chatMessageList = chatMessageService.chatMessageList(message);
////                for (MessageResponseDto messageResponseDto : chatMessageList) {
////                    System.out.println(messageResponseDto.getMessage());
////                    System.out.println(messageResponseDto.getType());
////                    System.out.println(messageResponseDto.getRoomId());
////                }
//
//                //   messagingTemplate.convertAndSend("/topic/greetings/" + message.getRoomId(),chatMessageList);
//            } else {
//                // 존재하지않는다면 입장시간 저장
//                System.out.println("Enter가 들어왔는데================방 입장 정보가 없을때 실행합니다.=====================================");
////                chatRoomJoinService.saveEnterTime(message);
//
//            }
//
//            // 방을 구별해주기 위해서 @SendTo를 쓰지 않고 SimpMessageSendingOperations를 사용해서 방 구별을 해줄 수 있게 함 ex) "/topic/greetings+roomId"
//
//
//
//        }else{
//            System.out.println("=========================Talk message==============================");
//            MessageResponseDto messageResponseDto = new MessageResponseDto(
//                    chatMessage.getMessageId()
//                    ,chatMessage.getMessage()
//                    , chatMessage.getCreatedAt()
//                    , chatMessage.getUser().getUsername()
//                    , chatMessage.getChatroom().getChatRoomId()
//                    , chatMessage.getMessageType());
//
//
//            chatMessageList.add(messageResponseDto);
//        }
//
//        messagingTemplate.convertAndSend("/topic/greetings/" + message.getRoomId(),chatMessageList);
//
//
//    }
}