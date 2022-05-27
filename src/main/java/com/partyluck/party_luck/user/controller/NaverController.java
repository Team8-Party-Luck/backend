package com.partyluck.party_luck.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.partyluck.party_luck.service.user.NaverUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class NaverController {
    private final NaverUserService naverUserService;

    @RequestMapping(value="/naver/callback", method=RequestMethod.GET)
   public String loginPOSTNaver(@RequestParam("code") String code,@RequestParam("state") String state,final HttpServletResponse responseh) throws JsonProcessingException, AssertionError {
        return naverUserService.loginWithNaver(code,state,responseh);
    }
}

