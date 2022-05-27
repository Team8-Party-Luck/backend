package com.partyluck.party_luck.party;

import com.partyluck.party_luck.party.requestDto.LocalSearchDto;
import com.partyluck.party_luck.party.requestDto.PartyRequestDto;
import com.partyluck.party_luck.party.responseDto.PartyDetailsResponseDto;
import com.partyluck.party_luck.party.responseDto.PartyResponseDto;
import com.partyluck.party_luck.party.responseDto.UserlistResultDto;
import com.partyluck.party_luck.party.responseDto.ResponseDto;
import com.partyluck.party_luck.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class PartyController {
    private final PartyService partyService;
    //파티 등록
    @PostMapping("/api/party")
    public ResponseDto registerParty(PartyRequestDto dto,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails)throws IOException {
        if(!partyService.checkRegister(dto))
            return new ResponseDto(false,400,"값이 유효하지 않습니다");
        return partyService.registerParty(dto,userDetails.getId());
    }
    //파티 일반 조회(비로그인)
    @GetMapping("/api/parties/raw/{page}")
    public PartyResponseDto RawPartyView(@PathVariable Integer page){
        return partyService.RawPartyView(page);
    }
    //파티 지역 조회
    @PostMapping("/home/parties/local")
    public PartyResponseDto LocalPartyView(@AuthenticationPrincipal UserDetailsImpl userDetails, LocalSearchDto localSearchDto){
        return partyService.LocalPartyView(userDetails.getId(),localSearchDto);
    }
    //좋아요한 파티 조회
    @GetMapping("/api/parties/sub")
    public PartyResponseDto mySubParty(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.mySubParty(userDetails.getId());
    }
    //내가 만든 파티 조회
    @GetMapping("/api/parties/history/creation")
    public PartyResponseDto myHostParty(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.myHostParty(userDetails.getId());
    }
    //참여할 파티 조회
    @GetMapping("/home/parties/join")
    public PartyResponseDto willjoinParty(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.willjoinParty(userDetails.getId());
    }
    //참가한 파티 조회
    @GetMapping("/api/parties/history/in")
    public PartyResponseDto joinedParty(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.joinedParty(userDetails.getId());
    }
    //파티 삭제
    @DeleteMapping("/api/party/{partyid}")
    public ResponseDto deleteParty(@PathVariable("partyid") Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.deleteParty(id,userDetails.getId());
    }
    //파티 참가
    @GetMapping("/api/party/in/{partyid}")
    public PartyDetailsResponseDto PartyJoin(@PathVariable("partyid") Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.PartyJoin(id,userDetails.getId());
    }
    //파티 참가 취소
    @DeleteMapping("/api/party/out/{partyid}")
    public PartyDetailsResponseDto PartyOut(@PathVariable("partyid") Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.PartyOut(id,userDetails.getId());
    }
    //파티 좋아요
    @GetMapping("/api/party/sub/{partyid}")
    public PartyDetailsResponseDto likeParty(@PathVariable("partyid") Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.likeParty(id,userDetails.getId());
    }
    //파티 상세보기
    @GetMapping("/api/party/details/{partyid}")
    public PartyDetailsResponseDto PartyDetail(@PathVariable("partyid") Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return partyService.PartyDetail(id,userDetails.getId());
    }
    //파티 수정
    @PutMapping("/api/party/{partyid}")
    public ResponseDto modifyParty(@PathVariable("partyid") Long id, PartyRequestDto dto) throws IOException {
        if(!partyService.checkModify(dto))
            return new ResponseDto(false,400,"값이 유효하지 않습니다");
        return partyService.modifyParty(id,dto);
    }
    //파티 참가인원 보기
    @GetMapping("/api/party/userlist/{partyid}")
    public UserlistResultDto Userlist(@PathVariable Long partyid){
        return partyService.Userlist(partyid);
    }


}