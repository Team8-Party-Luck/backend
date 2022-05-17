package com.partyluck.party_luck.websocket.dto.reponse;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChatRoomResponseDto {
    private String chatRoomId;
    private String senderNickname;
    private String image;
    private String creatdAt;
    private String lastMessage;
}
