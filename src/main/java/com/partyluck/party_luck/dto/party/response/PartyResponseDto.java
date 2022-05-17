package com.partyluck.party_luck.dto.party.response;

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
