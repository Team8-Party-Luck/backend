package com.partyluck.party_luck.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PartyResponseDto {
//    private PartyResponseResultDto[] result;
    List<PartyResponseResultDto> results;
}
