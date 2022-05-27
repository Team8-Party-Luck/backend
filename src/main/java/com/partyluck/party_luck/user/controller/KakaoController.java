package com.partyluck.party_luck.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.partyluck.party_luck.user.domain.User;
import com.partyluck.party_luck.security.UserDetailsImpl;
import com.partyluck.party_luck.security.jwt.JwtTokenUtils;
import com.partyluck.party_luck.user.service.KakaoUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequiredArgsConstructor
public class KakaoController {
    private final KakaoUserService kakaoUserService;

    @GetMapping("/auth/kakao")
    public String kakaoLogin(@RequestParam String code, final HttpServletResponse response) throws JsonProcessingException {
        User user=kakaoUserService.kakaoLogin(code);
        UserDetailsImpl userDetails=new UserDetailsImpl(user);
        final String token = JwtTokenUtils.generateJwtToken(userDetails);
        System.out.println(token);
        response.addHeader("Authorization", "BEARER" + " " + token);
        System.out.println(response.getStatus());
        return token;
    }
}
