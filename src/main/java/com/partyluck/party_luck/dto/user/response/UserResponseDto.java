package com.partyluck.party_luck.dto.user.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponseDto {
    private boolean ok;
    private UserResponseResultDto result;
    public UserResponseDto(boolean ok,UserResponseResultDto dto){
        this.ok=ok;
        this.result=dto;
    }

}
