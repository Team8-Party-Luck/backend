package com.partyluck.party_luck.websocket.domain;

import com.partyluck.party_luck.websocket.dto.MessageRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Setter
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    private ChatRoom chatroom; // 방번호

    @Column
    private MessageType messageType;

    @Column
    private String message; // 메시지

    @Column
    private String createdAt;

    @Column
    private Long senderId;


    // 메시지 타입 : 입장, 채팅
    public enum MessageType {
        ENTER, TALK, QUIT
    }

    @Builder
    public ChatMessage(ChatRoom chatRoom, Long senderId, MessageRequestDto message){
        this.senderId = senderId;
        this.chatroom = chatRoom;
        this.message = message.getMessage();
        this.createdAt = message.getCreatedAt();
        this.messageType = message.getType();
    }
}