package com.partyluck.party_luck.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.partyluck.party_luck.domain.User;
import com.partyluck.party_luck.dto.*;
import com.partyluck.party_luck.security.UserDetailsImpl;
import com.partyluck.party_luck.security.jwt.JwtTokenUtils;
import com.partyluck.party_luck.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import com.partyluck.party_luck.service.KakaoUserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
public class UserController {
    private final UserService userService;
    private final KakaoUserService kakaoUserService;

    @Autowired
    public UserController(UserService userService, KakaoUserService kakaoUserService) {
        this.userService = userService;
        this.kakaoUserService = kakaoUserService;
    }


    @ResponseBody
    @GetMapping("/user/kakao/callback")
    public String kakaoLogin(@RequestParam String code, final HttpServletResponse response) throws JsonProcessingException {
        User user=kakaoUserService.kakaoLogin(code);
        UserDetailsImpl userDetails=new UserDetailsImpl(user);
        final String token = JwtTokenUtils.generateJwtToken(userDetails);
        System.out.println(token);
        response.addHeader("Authorization", "BEARER" + " " + token);
        System.out.println(response.getStatus());
        return token;
    }

    @PostMapping("/api/user/initial")
    public ResponseDto initialRegister(@RequestParam("image")MultipartFile multipartFile, InitialDto dto,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails)throws IOException {
        return userService.initialRegister(multipartFile, dto,userDetails);

    }
    @GetMapping("/api/user/initial")
    public InitialResponseDto myinitial(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.myinitial(userDetails.getId());
    }
    @PutMapping("/api/user/initial")
    public ResponseDto modifiyinitial(@RequestParam("image")MultipartFile multipartFile, InitialDto dto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return userService.modifyinitial(multipartFile,dto,userDetails.getId());
    }
    @GetMapping("/api/user")
    public UserResponseDto userview(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.userview(userDetails.getId());
    }
    @PutMapping("/api/user")
    public ResponseDto modifyuser(@AuthenticationPrincipal UserDetailsImpl userDetails,ModifyUserRequestDto dto){
        return userService.modifyuser(userDetails.getId(),dto);
    }
    @Transactional
    @DeleteMapping("/api/user")
    public ResponseDto deleteuser(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.deleteuser(userDetails.getId());
    }

}
