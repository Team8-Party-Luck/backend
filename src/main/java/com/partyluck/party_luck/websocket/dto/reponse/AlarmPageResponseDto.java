package com.partyluck.party_luck.websocket.dto.reponse;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class AlarmPageResponseDto {

    private String alarmMessage;
    private String title;
    private String store;
    private String image;
    private String createdAt;

    public AlarmPageResponseDto(String title, String store, String image, String alarmMessage, String createdAt) {
        this.title = title;
        this.store = store;
        this.image = image;
        this.alarmMessage = alarmMessage;
        this.createdAt = createdAt;
    }
}
