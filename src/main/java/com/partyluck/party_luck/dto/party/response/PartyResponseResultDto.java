package com.partyluck.party_luck.dto.party.response;

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
    private String desc;
    private String store;
    private String address;
    private String date;
    private String time;
    private String meeting;
    private String[] image;
    private boolean ishost;
    private boolean issub;
    private String age;
    private String gender;


}
