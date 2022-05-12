package com.partyluck.party_luck.websocket.service;


import com.partyluck.party_luck.domain.Party;
import com.partyluck.party_luck.domain.PartyJoin;
import com.partyluck.party_luck.repository.PartyJoinRepository;
import com.partyluck.party_luck.repository.PartyRepository;
import com.partyluck.party_luck.security.UserDetailsImpl;
import com.partyluck.party_luck.websocket.domain.ChatRoom;
import com.partyluck.party_luck.websocket.dto.ChatRoomResponseDto;
import com.partyluck.party_luck.websocket.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final PartyJoinRepository partyJoinRepository;
    private final PartyRepository partyRepository;

    // 해당 유저의 채팅방 목록 불러오기
    public List<ChatRoomResponseDto> readChatRoomList(UserDetailsImpl userDetails) {
        List<ChatRoomResponseDto> chatRoomResponseDtoList = new ArrayList<>();
        // 유저아이디로  partyJoin테이블을 조회하여 참가한 party들을 불러온다.
        List<PartyJoin> partyJoinList = partyJoinRepository.findPartyJoinsByUser(userDetails.getUser());
        for(PartyJoin partyJoin : partyJoinList) {
            ChatRoom foundChatRoom = chatRoomRepository.findChatRoomByPartyId(partyJoin.getParty().getId()).orElseThrow(
                    () -> new IllegalArgumentException("해당 채팅 방을 찾을 수 없습니다.")
            );
            Party foundParty = partyRepository.findById(partyJoin.getParty().getId()).orElseThrow(
                    () -> new IllegalArgumentException("해당 파티를 조회할 수 없습니다.")
            );

            // Builder Annotation 사용
            ChatRoomResponseDto chatRoomResponseDto = ChatRoomResponseDto.builder()
                    .chatRoomId(foundChatRoom.getChatRoomId())
                    .title(foundParty.getTitle())
//                    .image(foundParty.getPartyImagesList().get(0).getImageUrl())
                    .image("")
                    .creatdAt("")
                    .lastMessage("")
                    .build();
            chatRoomResponseDtoList.add(chatRoomResponseDto);

        }
        return chatRoomResponseDtoList;
    }
}
