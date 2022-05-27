package com.partyluck.party_luck.chatroom.responseDto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChatRoomResponseDto {
    private String chatRoomId;
    private String senderNickname;
    private Long otherId;
    private String image;
    private String createdAt;
    private String lastMessage;
}
