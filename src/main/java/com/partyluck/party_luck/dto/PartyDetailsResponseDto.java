package com.partyluck.party_luck.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PartyDetailsResponseDto {
    private Long hostid;
    private Long partyid;
    private String title;
    private Integer capacity;
    private Integer memberCnt;
    private String address;
    private String store;
    private String desc;
    private String date;
    private String time;
    private String host;
    private String meeting;
    private String age;
    private String gender;
    private String xy;
    private String place_url;
    private String[] image;
    private boolean isJoin;
    private boolean isSub;
}
