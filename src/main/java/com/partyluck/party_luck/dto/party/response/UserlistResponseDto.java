package com.partyluck.party_luck.dto.party.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserlistResponseDto {
    private String nickname;
    private String age;
    private String gender;
    private String imageUrl;
    private String location;
    public UserlistResponseDto(String nickname, String age, String gender, String imageUrl, String location){
        this.nickname=nickname;
        this.age=age;
        this.gender=gender;
        this.imageUrl=imageUrl;
        this.location=location;
    }
}
