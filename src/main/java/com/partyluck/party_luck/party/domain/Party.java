package com.partyluck.party_luck.user.controller.party.domain;

import com.partyluck.party_luck.user.controller.party.requestDto.PartyRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Party {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private String date;

    private String time;

    private String store;

    private String address;

    private String meeting;

    private String gender;

    private String age;

    private String place_url;

    private String xy;

    private Long userid;

    @OneToMany(mappedBy = "party")
    private List<PartyJoin> partyJoinList;

    @OneToMany(mappedBy = "party")
    private List<Subscribe> subscribeList;

    public Party(PartyRequestDto dto, long id){
        this.title=dto.getTitle();
        this.capacity=dto.getCapacity();
        this.date=dto.getDate();
        this.time=dto.getTime();
        List<String> tmp=dto.getAge();
        Collections.sort(tmp);
        String s="";
        for(int i=0;i<tmp.size();i++)
            s+=tmp.get(i)+" ";
        this.age=s.substring(0,s.length()-1);
        this.gender=dto.getGender();
        this.meeting=dto.getMeeting();
        this.place_url=dto.getPlace_url();
        this.store=dto.getStore();
        this.description=dto.getDesc();
        this.userid=id;
        this.address=dto.getAddress();
        this.xy=dto.getXy();
    }
}