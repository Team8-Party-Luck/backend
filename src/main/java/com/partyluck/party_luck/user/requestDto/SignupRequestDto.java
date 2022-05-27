package com.partyluck.party_luck.user.requestDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequestDto {
    @Email(message = "올바른 형식의 이메일 주소를 입력 하세요")
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}",message = "비밀번호는 8자 이상,영문,숫자,특수문자 포함할 것!")
    private String password;

    private String passwordCheck;

    @Pattern(regexp = "^.{2,6}",message = "닉네임은 2자 이상 6자 미만입니다!")
    private String nickname;




}
