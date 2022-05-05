package com.partyluck.party_luck.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.partyluck.party_luck.dto.*;
import com.partyluck.party_luck.security.UserDetailsImpl;
import com.partyluck.party_luck.service.PartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

@RestController
public class PartyController {

    private final PartyService partyService;

    @Autowired
    public PartyController(PartyService partyService) {
        this.partyService = partyService;
    }

    @PostMapping("/api/party")
    public ResponseDto registerparty(
                                     PartyRequestDto dto,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails)throws IOException {
        return partyService.registerparty(dto,userDetails.getId());
    }
    @GetMapping("/api/parties")
    public PartyResponseDto rawpartyview(){
        return partyService.rawpartyview();
    }
    @GetMapping("/home/parties/local")
    public PartyResponseDto partyview(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.partyview(userDetails.getId());
    }
    @GetMapping("/api/parties/sub")
    public PartyResponseDto mysubparty(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.mysubparty(userDetails.getId());
    }
    @GetMapping("/api/parties/history/creation")
    public PartyResponseDto myhostparty(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.myhostparty(userDetails.getId());
    }
    @GetMapping("/home/parties/join")
    public PartyResponseDto willjoinparty(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.willjoinparty(userDetails.getId());
    }
    @GetMapping("/api/parties/history/in")
    public PartyResponseDto joinedparty(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.joinedparty(userDetails.getId());
    }

    @Transactional
    @DeleteMapping("/api/party/{partyid}")
    public ResponseDto deleteparty(@PathVariable("partyid") Long id){
        return partyService.deleteparty(id);
    }

    @PostMapping("/api/party/in/{partyid}")
    public String partyjoin(@PathVariable("partyid") Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.partyjoin(id,userDetails.getId());
    }
    @Transactional
    @DeleteMapping("/api/party/out/{partyid}")
    public String partyout(@PathVariable("partyid") Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.partyout(id,userDetails.getId());
    }

    @Transactional
    @PostMapping("/api/party/sub/{partyid}")
    public String likeparty(@PathVariable("partyid") Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.likeparty(id,userDetails.getId());
    }

    @GetMapping("/api/party/details/{partyid}")
    public PartyDetailsResponseDto partydetail(@PathVariable("partyid") Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.partydetail(id,userDetails.getId());
    }
    @PutMapping("/api/party/{partyid}")
    public ResponseDto modifyparty(@PathVariable("partyid") Long id, PartyModifyDto dto) throws IOException {
        return partyService.modifyparty(id,dto);

    }
    @PostMapping("/api/apis")
    public void testtest(@RequestParam("data") String dto) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        SignupRequestDto dto1=objectMapper.readValue(dto,SignupRequestDto.class);
        System.out.println(dto1.getNickname());
        System.out.println(dto1.getEmail());
        System.out.println(dto1.getPassword());
        System.out.println(dto1.getPasswordCheck());
    }

}
