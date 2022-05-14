package com.partyluck.party_luck.dto.user.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseResultDto {
    private Long userid;
    private String email;
    private String nickname;
    private String password;
}
