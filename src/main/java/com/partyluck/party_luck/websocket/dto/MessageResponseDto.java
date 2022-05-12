package com.partyluck.party_luck.websocket.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MessageResponseDto {
    private String nickname;
    private String message;
    private String createdAt;

    @Builder
    public MessageResponseDto(String nickname, String message, String createdAt) {
        this.nickname = nickname;
        this.message = message;
        this.createdAt = createdAt;
    }
}
