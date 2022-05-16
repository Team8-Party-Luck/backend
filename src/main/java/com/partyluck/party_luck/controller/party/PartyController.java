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
//파티 등록
    @PostMapping("/api/party")
    public ResponseDto registerparty(PartyRequestDto dto,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails)throws IOException {
        if(!partyService.checkregister(dto))
            return new ResponseDto(false,400,"값이 유효하지 않습니다");
        return partyService.registerparty(dto,userDetails.getId());
    }
//파티 일반 조회(비로그인)
    @GetMapping("/api/parties/raw/{page}")
    public PartyResponseDto rawpartyview(@PathVariable Integer page){
        page=page-1;
        return partyService.rawpartyview(page);
    }
//파티 지역 조회
    @GetMapping("/home/parties/local")
    public PartyResponseDto partyview(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.partyview(userDetails.getId());
    }
//좋아요한 파티 조회
    @GetMapping("/api/parties/sub")
    public PartyResponseDto mysubparty(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.mysubparty(userDetails.getId());
    }
//내가 만든 파티 조회
    @GetMapping("/api/parties/history/creation")
    public PartyResponseDto myhostparty(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.myhostparty(userDetails.getId());
    }
//참여할 파티 조회
    @GetMapping("/home/parties/join")
    public PartyResponseDto willjoinparty(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.willjoinparty(userDetails.getId());
    }
//참가한 파티 조회
    @GetMapping("/api/parties/history/in")
    public PartyResponseDto joinedparty(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.joinedparty(userDetails.getId());
    }
//파티 삭제
    @DeleteMapping("/api/party/{partyid}")
    public ResponseDto deleteparty(@PathVariable("partyid") Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.deleteparty(id,userDetails.getId());
    }
//파티 참가
    @GetMapping("/api/party/in/{partyid}")
    public PartyDetailsResponseDto partyjoin(@PathVariable("partyid") Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.partyjoin(id,userDetails.getId());
    }
//파티 참가 취소
    @DeleteMapping("/api/party/out/{partyid}")
    public PartyDetailsResponseDto partyout(@PathVariable("partyid") Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.partyout(id,userDetails.getId());
    }
//파티 좋아요
    @GetMapping("/api/party/sub/{partyid}")
    public PartyDetailsResponseDto likeparty(@PathVariable("partyid") Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.likeparty(id,userDetails.getId());
    }
//파티 상세보기
    @GetMapping("/api/party/details/{partyid}")
    public PartyDetailsResponseDto partydetail(@PathVariable("partyid") Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.partydetail(id,userDetails.getId());
    }
//파티 수정
    @PutMapping("/api/party/{partyid}")
    public ResponseDto modifyparty(@PathVariable("partyid") Long id, PartyRequestDto dto) throws IOException {
        if(!partyService.checkmodify(dto))
            return new ResponseDto(false,400,"값이 유효하지 않습니다");
        return partyService.modifyparty(id,dto);
    }
//파티 참가인원 보기
    @GetMapping("/api/party/userlist/{partyid}")
    public List<UserlistResponseDto> userlist(@PathVariable Long partyid){
        return partyService.userlist(partyid);
    }


}
