package com.partyluck.party_luck.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.partyluck.party_luck.domain.User;
import com.partyluck.party_luck.security.UserDetailsImpl;
import com.partyluck.party_luck.security.jwt.JwtTokenUtils;
import com.partyluck.party_luck.service.user.KakaoUserService;
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
        System.out.println("11111");
        User user=kakaoUserService.kakaoLogin(code);
        System.out.println("22222");
        UserDetailsImpl userDetails=new UserDetailsImpl(user);
        final String token = JwtTokenUtils.generateJwtToken(userDetails);
        System.out.println("33333");
        System.out.println(token);
        response.addHeader("Authorization", "BEARER" + " " + token);
        System.out.println(response.getStatus());
        return token;
    }
}
