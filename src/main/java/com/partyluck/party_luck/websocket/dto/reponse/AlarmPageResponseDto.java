package com.partyluck.party_luck.websocket.dto.reponse;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@NoArgsConstructor
public class AlarmPageResponseDto {

    private String alarms;
    private String title;
    private String store;
    private String image;
    private String createdAt;

    public AlarmPageResponseDto(String title, String store, String image, String alarms, String createdAt) {
        this.title = title;
        this.store = store;
        this.image = image;
        this.alarms=alarms;
        this.createdAt = createdAt;
    }
}
