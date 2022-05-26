package com.partyluck.party_luck.dto.party.response;

import com.partyluck.party_luck.domain.Party;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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
    private Long hostId;

    public PartyResponseResultDto(Party p,String[] ist){
        this.partyId=p.getId();
        this.title=p.getTitle();
        this.capacity=p.getCapacity();
        this.desc=p.getDescription();
        this.store=p.getStore();
        String[] tmp=p.getAddress().split(" ");
        this.address=tmp[0]+" "+tmp[1];
        this.date=p.getDate();
        this.time=p.getTime();
        this.meeting=p.getMeeting();
        this.age=p.getAge();
        this.gender=p.getGender();
        this.image=ist;
        this.hostId=p.getUserid();
    }
    public PartyResponseResultDto(Party p,String[] ist,boolean ishost,boolean issub){
        this.partyId=p.getId();
        this.title=p.getTitle();
        this.capacity=p.getCapacity();
        this.desc=p.getDescription();
        this.store=p.getStore();
        String[] tmp=p.getAddress().split(" ");
        this.address=tmp[0]+" "+tmp[1];
        this.date=p.getDate();
        this.time=p.getTime();
        this.meeting=p.getMeeting();
        this.age=p.getAge();
        this.gender=p.getGender();
        this.image=ist;
        this.hostId=p.getUserid();
        this.ishost=ishost;
        this.issub=issub;
    }


}