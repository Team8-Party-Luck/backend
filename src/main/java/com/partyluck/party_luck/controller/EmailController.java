package com.partyluck.party_luck.controller;

import javax.servlet.http.HttpServletRequest;

import com.partyluck.party_luck.email.EmailServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.partyluck.party_luck.email.*;

@Controller
@RequestMapping("/auth/*")
public class EmailController {
    @Autowired
    EmailService service;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/email")
    @ResponseBody
    public void emailConfirm(String userId)throws Exception{
        logger.info("post emailConfirm");
        System.out.println("전달 받은 이메일 : "+userId);
        service.sendSimpleMessage(userId);
    }
    @PostMapping("/email/check")
    @ResponseBody
    public String verifyCode(String code) {
        logger.info("Post verifyCode");

        int result = 0;
        String results="인증이 실패했습니다...";
        System.out.println("code : "+code);
        System.out.println("code match : "+ EmailServiceImpl.ePw.equals(code));
        if(EmailServiceImpl.ePw.equals(code)) {
            result =1;
            results="인증이 성공했습니다!";
        }

        return results;
    }
}