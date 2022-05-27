package com.partyluck.party_luck.chatmessage.requestDto;


import com.partyluck.party_luck.chatmessage.ChatMessage;
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
