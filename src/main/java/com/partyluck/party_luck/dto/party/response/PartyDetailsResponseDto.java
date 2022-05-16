package com.partyluck.party_luck.dto.party.response;

import com.partyluck.party_luck.domain.Party;
import com.partyluck.party_luck.repository.PartyJoinRepository;
import com.partyluck.party_luck.repository.UserRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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
    private String[] userimageurls;
    private boolean isJoin;
    private boolean isSub;


    public PartyDetailsResponseDto(Party party, long id, UserRepository userRepository, PartyJoinRepository partyJoinRepository,String[] ist,String[] urls,boolean sub,boolean join){
        this.title=party.getTitle();
        this.store=party.getStore();
        this.desc=party.getDescription();
        this.date=party.getDate();
        this.time=party.getTime();
        this.meeting=party.getMeeting();
        this.age=party.getAge();
        this.gender=party.getGender();
        this.xy=party.getXy();
        this.partyid=id;
        this.place_url=party.getPlace_url();
        this.capacity=party.getCapacity();
        String[] addtmp = party.getAddress().split(" ");
        this.address=addtmp[0] + " " + addtmp[1];
        this.host=userRepository.findById(party.getUserid()).orElse(null).getNickname();
        this.hostid=userRepository.findById(party.getUserid()).orElse(null).getId();
        this.memberCnt=partyJoinRepository.findAllByParty(party).size();
        this.image=ist;
        this.userimageurls=urls;
        this.isSub=sub;
        this.isJoin=join;
    }

}
