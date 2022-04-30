package com.partyluck.party_luck.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PartyRequestDto {
    private String title;
    private Integer capacity;
//    private String location;
    private String store;
    private String date;
    private String time;
    private String meeting;
    private String desc;
}
