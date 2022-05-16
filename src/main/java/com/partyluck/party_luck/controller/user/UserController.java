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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


//테스트용 회원가입
    @PostMapping("/api/user")
    public ResponseDto signupUser(@Valid @RequestBody SignupRequestDto dto){
        return userService.registerUser(dto);
    }

//상세정보 입력
    @PostMapping("/api/user/initial")
    public ResponseDto initialRegister(@RequestBody InitialDto dto,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails)throws IOException {
        return userService.initialRegister(dto,userDetails);

    }
//상세정보 보기
    @GetMapping("/api/user/initial")
    public InitialResponseDto myinitial(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.myinitial(userDetails.getId());
    }
//상세정보 수정
    @PutMapping("/api/user/initial")
    public ResponseDto modifiyinitial(InitialDto dto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return userService.modifyinitial(dto,userDetails.getId());
    }
//기본정보 보기
    @GetMapping("/api/user")
    public UserResponseDto userview(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.userview(userDetails.getId());
    }
//기본정보 수정
    @PutMapping("/api/user")
    public ResponseDto modifyuser(@AuthenticationPrincipal UserDetailsImpl userDetails, ModifyUserRequestDto dto){
        return userService.modifyuser(userDetails.getId(),dto);
    }
//회원 탈퇴
    @DeleteMapping("/api/user")
    public ResponseDto deleteuser(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.deleteUser(userDetails.getId());
    }
}
