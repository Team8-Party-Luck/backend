package com.partyluck.party_luck.domain;

import com.partyluck.party_luck.dto.user.request.InitialDto;
import com.partyluck.party_luck.security.UserDetailsImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class InitialInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long initial_info_id;

    private String profile_img;

    private String food;

    private String age;

    private String gender;

    private String sns_url;

    private String region;

    private String city;

    private String intro;

    @Column(name="user_id")
    private Long userId;

    public InitialInfo(InitialDto dto, UserDetailsImpl userDetails){
        this.age=dto.getAge();
        this.gender=dto.getGender();
        this.sns_url=dto.getSns();
        this.intro=dto.getIntro();
        this.city=dto.getCity();
        this.region=dto.getRegion();
        this.userId=userDetails.getId();
        String s="";
        for(int i=0;i<dto.getFood().size();i++)
            s=s+dto.getFood().get(i)+" ";
        this.food=s.substring(0,s.length()-1);
    }

}