package com.partyluck.party_luck.dto.user.response;

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
}
