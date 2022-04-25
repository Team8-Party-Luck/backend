package com.partyluck.party_luck.controller;

import com.partyluck.party_luck.dto.SignupRequestDto;
import com.partyluck.party_luck.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/user")
    public String signupUser(SignupRequestDto dto){

        userService.registerUser(dto);


        return "Success";
    }
}
