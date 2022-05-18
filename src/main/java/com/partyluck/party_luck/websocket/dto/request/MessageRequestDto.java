package com.partyluck.party_luck.websocket.dto.request;


import com.partyluck.party_luck.websocket.domain.ChatMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageRequestDto {
    private String chatRoomId;
    private ChatMessage.MessageType type;
    private String message;
    private String createdAt;
}
