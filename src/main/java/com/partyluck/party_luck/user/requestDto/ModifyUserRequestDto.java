package com.partyluck.party_luck.user.requestDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ModifyUserRequestDto {
//    private String email;
    private String nickname;
    private String password;
    private String newPassword;
}
