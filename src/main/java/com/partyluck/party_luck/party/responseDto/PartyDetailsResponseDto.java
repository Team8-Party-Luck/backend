package com.partyluck.party_luck.party.responseDto;

import com.partyluck.party_luck.party.repository.PartyJoinRepository;
import com.partyluck.party_luck.party.domain.Party;
import com.partyluck.party_luck.user.repository.UserRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

import static com.partyluck.party_luck.party.DefaultImage.DefaultImages;

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
    private boolean isDefault;
    private boolean isJoin;
    private boolean isSub;


    public PartyDetailsResponseDto(Party party, long id, UserRepository userRepository, PartyJoinRepository partyJoinRepository, String[] ist, String[] urls, boolean sub, boolean join){
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
        if(ist!=null&&ist.length!=0&&Arrays.asList(DefaultImages).contains(ist[0]))
            this.isDefault=true;
        else
            this.isDefault=false;
        this.userimageurls=urls;
        this.isSub=sub;
        this.isJoin=join;
    }

}