package com.partyluck.party_luck.websocket.dto.request;


import com.partyluck.party_luck.websocket.domain.ChatMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequestDto {
    private String roomId;
    private ChatMessage.MessageType type;
    private String message;
    private String createdAt;
}
