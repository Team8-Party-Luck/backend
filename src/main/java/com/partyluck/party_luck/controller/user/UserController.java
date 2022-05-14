package com.partyluck.party_luck.controller.user;

import com.partyluck.party_luck.dto.*;
import com.partyluck.party_luck.dto.user.request.InitialDto;
import com.partyluck.party_luck.dto.user.request.ModifyUserRequestDto;
import com.partyluck.party_luck.dto.user.request.SignupRequestDto;
import com.partyluck.party_luck.dto.user.response.InitialResponseDto;
import com.partyluck.party_luck.dto.user.response.UserResponseDto;
import com.partyluck.party_luck.security.UserDetailsImpl;
import com.partyluck.party_luck.service.user.UserService;
import lombok.RequiredArgsConstructor;
import com.partyluck.party_luck.service.user.KakaoUserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final KakaoUserService kakaoUserService;


//테스트용 회원가입
    @PostMapping("/api/user")
    public ResponseDto signupUser(@Valid @RequestBody SignupRequestDto dto){

        return userService.registerUser(dto);

    }


    @PostMapping("/api/user/initial")
    public ResponseDto initialRegister(@RequestBody InitialDto dto,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails)throws IOException {
        return userService.initialRegister(dto,userDetails);

    }

    @GetMapping("/api/user/initial")
    public InitialResponseDto myinitial(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.myinitial(userDetails.getId());
    }

    @PutMapping("/api/user/initial")
    public ResponseDto modifiyinitial(InitialDto dto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return userService.modifyinitial(dto,userDetails.getId());
    }

    @GetMapping("/api/user")
    public UserResponseDto userview(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.userview(userDetails.getId());
    }

    @PutMapping("/api/user")
    public ResponseDto modifyuser(@AuthenticationPrincipal UserDetailsImpl userDetails, ModifyUserRequestDto dto){
        return userService.modifyuser(userDetails.getId(),dto);
    }

    @DeleteMapping("/api/user")
    public ResponseDto deleteuser(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.deleteUser(userDetails.getId());
    }

}
