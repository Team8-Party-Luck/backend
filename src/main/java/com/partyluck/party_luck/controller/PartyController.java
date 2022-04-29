package com.partyluck.party_luck.controller;

import com.partyluck.party_luck.dto.PartyRequestDto;
import com.partyluck.party_luck.dto.PartyResponseDto;
import com.partyluck.party_luck.dto.ResponseDto;
import com.partyluck.party_luck.security.UserDetailsImpl;
import com.partyluck.party_luck.service.PartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class PartyController {

    private final PartyService partyService;

    @Autowired
    public PartyController(PartyService partyService) {
        this.partyService = partyService;
    }

    @PostMapping("/api/party")
    public ResponseDto registerparty(@RequestParam("image") MultipartFile[] multipartFile,
                                     PartyRequestDto dto,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails)throws IOException {
        return partyService.registerparty(multipartFile,dto,userDetails.getId());
    }
    @GetMapping("/api/party")
    public PartyResponseDto partyview(){
        return partyService.partyview();
    }
    @DeleteMapping("/api/party/{partyid}")
    public ResponseDto deleteparty(@PathVariable("partyid") Long id){
        return partyService.deleteparty(id);
    }
    @PostMapping("/api/party/in/{partyid}")
    public String partyjoin(@PathVariable("partyid") Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.partyjoin(id,userDetails.getId());
    }
    @Transactional
    @DeleteMapping("/api/party/in/{partyid}")
    public String partyout(@PathVariable("partyid") Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.partyout(id,userDetails.getId());
    }

}
