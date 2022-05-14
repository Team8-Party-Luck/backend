package com.partyluck.party_luck.controller.party;

import com.partyluck.party_luck.dto.*;
import com.partyluck.party_luck.dto.party.request.PartyRequestDto;
import com.partyluck.party_luck.dto.party.response.PartyDetailsResponseDto;
import com.partyluck.party_luck.dto.party.response.PartyResponseDto;
import com.partyluck.party_luck.dto.party.response.UserlistResponseDto;
import com.partyluck.party_luck.security.UserDetailsImpl;
import com.partyluck.party_luck.service.party.PartyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PartyController {

    private final PartyService partyService;



    @PostMapping("/api/party")
    public ResponseDto registerparty(PartyRequestDto dto,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails)throws IOException {
        if(!partyService.checkregister(dto))
            return new ResponseDto(false,400,"값이 유효하지 않습니다");
        return partyService.registerparty(dto,userDetails.getId());
    }

    @GetMapping("/api/parties/raw/{page}")
    public PartyResponseDto rawpartyview(@PathVariable Integer page){
        page=page-1;
        return partyService.rawpartyview(page);
    }

    @GetMapping("/home/parties/local")
    public PartyResponseDto partyview(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.partyview(userDetails.getId(),0);
    }

    @GetMapping("/api/parties/sub")
    public PartyResponseDto mysubparty(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.mysubparty(userDetails.getId(),0);
    }

    @GetMapping("/api/parties/history/creation")
    public PartyResponseDto myhostparty(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.myhostparty(userDetails.getId(),0);
    }

    @GetMapping("/home/parties/join")
    public PartyResponseDto willjoinparty(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.willjoinparty(userDetails.getId(),0);
    }

    @GetMapping("/api/parties/history/in")
    public PartyResponseDto joinedparty(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.joinedparty(userDetails.getId(),0);
    }

    @DeleteMapping("/api/party/{partyid}")
    public ResponseDto deleteparty(@PathVariable("partyid") Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.deleteparty(id,userDetails.getId());
    }

    @GetMapping("/api/party/in/{partyid}")
    public PartyDetailsResponseDto partyjoin(@PathVariable("partyid") Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.partyjoin(id,userDetails.getId());
    }

    @DeleteMapping("/api/party/out/{partyid}")
    public PartyDetailsResponseDto partyout(@PathVariable("partyid") Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.partyout(id,userDetails.getId());
    }

    @GetMapping("/api/party/sub/{partyid}")
    public PartyDetailsResponseDto likeparty(@PathVariable("partyid") Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.likeparty(id,userDetails.getId());
    }

    @GetMapping("/api/party/details/{partyid}")
    public PartyDetailsResponseDto partydetail(@PathVariable("partyid") Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.partydetail(id,userDetails.getId());
    }

    @PutMapping("/api/party/{partyid}")
    public ResponseDto modifyparty(@PathVariable("partyid") Long id, PartyRequestDto dto) throws IOException {
        if(!partyService.checkmodify(dto))
            return new ResponseDto(false,400,"값이 유효하지 않습니다");
        return partyService.modifyparty(id,dto);
    }

    @GetMapping("/api/party/userlist/{partyid}")
    public List<UserlistResponseDto> userlist(@PathVariable Long partyid){
        return partyService.userlist(partyid);
    }


}
