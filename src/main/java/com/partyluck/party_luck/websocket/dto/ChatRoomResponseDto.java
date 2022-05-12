package com.partyluck.party_luck.websocket.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChatRoomResponseDto {
    private String chatRoomId;
    private String title;
    private String image;
    private String creatdAt;
    private String lastMessage;
}
