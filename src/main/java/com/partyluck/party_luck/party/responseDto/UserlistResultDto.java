package com.partyluck.party_luck.user.controller.party.responseDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UserlistResultDto {
    private Long hostId;
    private List<UserlistResponseDto> result;

    public UserlistResultDto(Long hostId, List<UserlistResponseDto> result){
        this.hostId=hostId;
        this.result=result;
    }
}