package com.partyluck.party_luck.party.responseDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class PartyResponseDto {
    List<PartyResponseResultDto> results;
    public PartyResponseDto(List<PartyResponseResultDto> results){
        this.results=results;
    }
}