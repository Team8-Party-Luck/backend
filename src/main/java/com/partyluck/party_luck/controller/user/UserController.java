package com.partyluck.party_luck.controller.user;

import com.partyluck.party_luck.dto.*;
import com.partyluck.party_luck.dto.user.request.InitialDto;
import com.partyluck.party_luck.dto.user.request.ModifyUserRequestDto;
import com.partyluck.party_luck.dto.user.request.ReportRequestDto;
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
    public ResponseDto InitialRegister(@RequestBody InitialDto dto,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails)throws IOException {
        return userService.InitialRegister(dto,userDetails);

    }
//상세정보 보기
    @GetMapping("/api/user/initial")
    public InitialResponseDto myInitial(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.myInitial(userDetails.getId());
    }
//상세정보 수정
    @PutMapping("/api/user/initial")
    public ResponseDto modifiyInitial(InitialDto dto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return userService.modifyInitial(dto,userDetails.getId());
    }
//기본정보 보기
    @GetMapping("/api/user")
    public UserResponseDto userView(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.userView(userDetails.getId());
    }
//기본정보 수정
    @PutMapping("/api/user")
    public ResponseDto modifyUser(@AuthenticationPrincipal UserDetailsImpl userDetails, ModifyUserRequestDto dto){
        return userService.modifyUser(userDetails.getId(),dto);
    }
//회원 탈퇴
    @DeleteMapping("/api/user")
    public ResponseDto deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.deleteUser(userDetails.getId());
    }

//유저 신고
    @PostMapping("/api/user/report")
    public ResponseDto reportUser(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody ReportRequestDto dto){
        return userService.reportUser(userDetails.getId(),dto);
    }
}
