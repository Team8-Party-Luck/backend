package com.partyluck.party_luck.dto.user.response;

import com.partyluck.party_luck.domain.InitialInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InitialResponseDto {
    private String[] food;
    private String age;
    private String gender;
    private String sns;
    private String image;
    private String intro;
    private String city;
    private String region;
    private String nickname;

    public InitialResponseDto(InitialInfo info){
        this.age=info.getAge();
        String[] foods=info.getFood().split(" ");
        this.food=foods;
        this.gender=info.getGender();
        this.image=info.getProfile_img();
        this.sns=info.getSns_url();
        this.intro=info.getIntro();
        this.city=info.getCity();
        this.region=info.getRegion();
    }

}
