package com.partyluck.party_luck.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PartyResponseResultDto {

    private Long partyId;
    private String title;
    private Integer capacity;
    private String location;
    private String date;
    private String time;
    private String meeting;
    private String[] image;


}
