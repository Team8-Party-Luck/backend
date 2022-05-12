package com.partyluck.party_luck.websocket.controller;


import com.partyluck.party_luck.security.UserDetailsImpl;
import com.partyluck.party_luck.websocket.dto.ChatRoomResponseDto;
import com.partyluck.party_luck.websocket.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 해당 유저의 채팅방 목록 불러오기
    @GetMapping("/chatroom/get")
    public List<ChatRoomResponseDto> readChatRoomList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.readChatRoomList(userDetails);
    }
}
