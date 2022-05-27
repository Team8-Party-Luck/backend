package com.partyluck.party_luck.user.responseDto;

import com.partyluck.party_luck.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponseResultDto {
    private Long userid;
    private String email;
    private String nickname;
    private String password;
    public UserResponseResultDto(User user,long id){
        this.email=user.getEmail();
        this.nickname=user.getNickname();
        this.password=user.getPassword();
        this.userid=id;
    }
}
