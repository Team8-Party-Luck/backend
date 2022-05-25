//package com.partyluck.party_luck.controller.user;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.partyluck.party_luck.service.user.NaverUserService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletResponse;
//
//@RestController
//@Slf4j
//@RequiredArgsConstructor
//public class NaverController {
//    private final NaverUserService naverUserService;
//
//
//    @RequestMapping(value="/", method= RequestMethod.GET)
//    public String index() {
//        log.info("home controller");
//        return "hi";
//    }
//
//
//   @RequestMapping(value="/naver/callback", method=RequestMethod.GET)
//   public String loginPOSTNaver(@RequestParam("code") String code,@RequestParam("state") String state,final HttpServletResponse responseh) throws JsonProcessingException, AssertionError { log.info("naver login!");
//        return naverUserService.loginWithNaver(code,state,responseh);
//    }
//}
//
