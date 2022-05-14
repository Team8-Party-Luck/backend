package com.partyluck.party_luck.dto.party.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserlistResponseDto {
    private String nickname;
    private String age;
    private String gender;
    private String imageUrl;
    private String location;
}
