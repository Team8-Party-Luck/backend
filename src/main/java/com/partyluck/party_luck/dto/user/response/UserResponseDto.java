package com.partyluck.party_luck.dto.user.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDto {
    private boolean ok;
    private UserResponseResultDto result;

}
