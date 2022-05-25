package com.partyluck.party_luck.websocket.dto.reponse;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ChatRoomUserInofoResponseDto {
    private String otherNickname;
    private String otherProfile;
    private Long userId;
}
