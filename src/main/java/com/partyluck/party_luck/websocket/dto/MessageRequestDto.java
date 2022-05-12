package com.partyluck.party_luck.websocket.dto;


import com.partyluck.party_luck.websocket.domain.ChatMessage;
import lombok.Getter;

@Getter
public class MessageRequestDto {
    private String roomId;
    private ChatMessage.MessageType type;
    private String message;
    private String createdAt;
}
