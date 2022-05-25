package com.partyluck.party_luck.websocket.dto.reponse;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MessageResponseDto {
//    private String nickname;
    private String message;
    private String createdAt;
    private Long userId;
    private String imageUrl;
    private String chatroomId;

    @Builder
    public MessageResponseDto(String message, String createdAt,Long userId,String imageUrl) {
//        this.nickname = nickname;
        this.message = message;
        this.createdAt = createdAt;
        this.userId=userId;
        this.imageUrl=imageUrl;
    }

    public MessageResponseDto(String message, String createdAt,Long userId,String imageUrl,String chatroomId) {
//        this.nickname = nickname;
        this.message = message;
        this.createdAt = createdAt;
        this.userId=userId;
        this.imageUrl=imageUrl;
        this.chatroomId=chatroomId;
    }
}
