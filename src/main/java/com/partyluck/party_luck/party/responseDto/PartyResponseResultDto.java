package com.partyluck.party_luck.party.responseDto;

import com.partyluck.party_luck.party.domain.Party;
import com.partyluck.party_luck.party.repository.PartyJoinRepository;
import com.partyluck.party_luck.party.repository.PartyRepository;
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
    private Integer memberCnt;

    public PartyResponseResultDto(Party p, String[] ist, PartyJoinRepository partyJoinRepository){
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
        this.memberCnt=partyJoinRepository.findAllByParty(p).size();
    }
    public PartyResponseResultDto(Party p,String[] ist,boolean ishost,boolean issub,PartyJoinRepository partyJoinRepository){
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
        this.memberCnt=partyJoinRepository.findAllByParty(p).size();
    }


}