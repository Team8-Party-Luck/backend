package com.partyluck.party_luck.chatroom;


import com.partyluck.party_luck.chatroom.responseDto.ChatRoomResponseDto;
import com.partyluck.party_luck.chatroom.responseDto.ChatRoomUserInofoResponseDto;
import com.partyluck.party_luck.security.UserDetailsImpl;
import com.partyluck.party_luck.chatroom.requestDto.ChatRoomRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 해당 유저의 채팅방 목록 불러오기
    @GetMapping("/chatroom/get")
    public List<ChatRoomResponseDto> readChatRoomList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        System.out.println("확인1");
        return chatRoomService.readChatRoomList(userDetails);
    }

    // 1:1 채팅방 생성하기
    @PostMapping("/chatroom/create")
    public ResponseEntity<?> createChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody ChatRoomRequestDto requestDto) {
        System.out.println("사용자 유저 아이디 : " + userDetails.getUser().getId());
        System.out.println("상대방 유저 아이디 : " + requestDto.getOtherId());
        String chatRoomId = chatRoomService.createChatRoom(userDetails, requestDto.getOtherId());
        Map<String, Object> result = new HashMap<>();
        result.put("result", "ok");
        if(chatRoomId.equals("")) {
            result.put("msg", "이미 채팅방이 존재합니다.");
            result.put("chatRoomId", chatRoomId);
        } else {
            result.put("msg", "성공적으로 채팅방이 개설되었습니다.");
            result.put("chatRoomId", chatRoomId);
        }

        return ResponseEntity.status(200).body(result);
    }

    @GetMapping("/chatroom/user/{chatRoomId}")
    public ChatRoomUserInofoResponseDto readChatRoomUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable String chatRoomId) {
        return chatRoomService.readChatRoomUserInfo(userDetails, chatRoomId);
    }
}
