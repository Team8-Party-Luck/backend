package com.partyluck.party_luck.websocket.service;


import com.partyluck.party_luck.domain.InitialInfo;
import com.partyluck.party_luck.domain.User;
import com.partyluck.party_luck.repository.InitialInfoRepository;
import com.partyluck.party_luck.repository.UserRepository;
import com.partyluck.party_luck.security.UserDetailsImpl;
import com.partyluck.party_luck.websocket.domain.ChatRoom;
import com.partyluck.party_luck.websocket.domain.JoinChatRoom;
import com.partyluck.party_luck.websocket.dto.reponse.ChatRoomResponseDto;
import com.partyluck.party_luck.websocket.repository.ChatRoomRepository;
import com.partyluck.party_luck.websocket.repository.JoinChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final UserRepository userRepository;
    private final InitialInfoRepository initialInfoRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final JoinChatRoomRepository joinChatRoomRepository;


    // 해당 유저의 채팅방 목록 불러오기
    @Transactional
    public List<ChatRoomResponseDto> readChatRoomList(UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        User otherUser = new User();

        List<ChatRoom> chatRoomList = new ArrayList<>();
        List<ChatRoomResponseDto> chatRoomResponseDtoList = new ArrayList<>();

        // 해당 유저의 JoinChatRoom 리스트를 불러온다
        List<JoinChatRoom> joinChatRoomList = joinChatRoomRepository.findJoinChatRoomsByUser(user);

        // 채팅방 리스트를 모두 가져온다.
        for(JoinChatRoom joinChatRoom : joinChatRoomList) {
            chatRoomList.add(joinChatRoom.getChatRoom());
        }

        for(ChatRoom chatRoom : chatRoomList) {
            // 상대유저의 닉네임을 찾아내야한다.
            List<JoinChatRoom> foundJoinChatRoomList = joinChatRoomRepository.findJoinChatRoomsByChatRoom_ChatRoomId(chatRoom.getChatRoomId());
            for(JoinChatRoom joinChatRoom : foundJoinChatRoomList) {
                if(!joinChatRoom.getUser().getId().equals(userDetails.getUser().getId())) {
                    otherUser = joinChatRoom.getUser();
                }
            }

            // 상대방 유저의 프로필 이미지를 가져오기 위해서 해당 유저의 initialInfo가 필요하다.
            InitialInfo otherInitialUserInfo = initialInfoRepository.findInitialInfoByUserId(otherUser.getId()).orElseThrow(
                    () -> new IllegalArgumentException("해당 유저의 이니셜정보가 없습니다.")
            );

            // Builder Annotation 사용
            ChatRoomResponseDto chatRoomResponseDto = ChatRoomResponseDto.builder()
                    .chatRoomId(chatRoom.getChatRoomId())
                    .senderNickname(otherUser.getNickname())
                    .image(otherInitialUserInfo.getProfile_img())
                    .creatdAt("")
                    .lastMessage("")
                    .otherId(otherUser.getId())
                    .build();
            chatRoomResponseDtoList.add(chatRoomResponseDto);
        }

        return chatRoomResponseDtoList;
    }

    // 1:1 채팅방 만들기(파티상세 페이지 -> 호스트문의하기, 유저리스트 페이지 -> 채팅하기 누르기)
    // 중복 채팅방이 없으면 새로운 채팅방을 만들고 그 채팅방 ID를 프론트엔드에게 전달한다.
    // 중복 채팅방이 있으면 기존의 채팅방 ID를 찾아 프론트엔드에게 전달한다.
    public String createChatRoom(UserDetailsImpl userDetails, Long otherId) {
        // 채팅 기록이 있는지 확인 - 기존에 있는 채팅인지 아닌지 판별하기(유저 두명이 다 있는 채팅방인지 판별해야한다.)
        User user = userDetails.getUser();
        User otherUser = userRepository.findById(otherId).orElseThrow(
                () -> new IllegalArgumentException("상대 유저가 존재하지 않습니다.")
        );

        // 클라이언트에게 전달할 채팅방 ID
        String chatRoomId = "";

        // 중복 채팅방 확인
        //List<JoinChatRoom> joinChatRoomList = joinChatRoomRepository.findJoinChatRoomsByUser_Id(user.getId()).orElse(null);

        Optional<JoinChatRoom> joinChatRoomUser = joinChatRoomRepository.findJoinChatRoomByUser_Id(user.getId());
        Optional<JoinChatRoom> joinChatRoomOtherUser = joinChatRoomRepository.findJoinChatRoomByUser_Id(otherId);

        if(joinChatRoomUser.isPresent() && joinChatRoomOtherUser.isPresent()) {
            String userChatRoomId = joinChatRoomUser.get().getChatRoom().getChatRoomId();
            String otherChatRoomId = joinChatRoomOtherUser.get().getChatRoom().getChatRoomId();

            if(userChatRoomId.equals(otherChatRoomId))
                chatRoomId = userChatRoomId;
        } else {
            System.out.println("기존 채팅방이 존재하지 않을 경우");
            // 중복 채팅방이 없다면 채팅방을 새로 만들어준다.
            // 채팅방 생성과 동시에 JoinChatRoom에 두명의 유저가 추가된다.
            ChatRoom chatRoom = new ChatRoom();
            JoinChatRoom joinChatRoomUserTwo = new JoinChatRoom(user, chatRoom);
            JoinChatRoom joinChatRoomOtherUserTwo = new JoinChatRoom(otherUser, chatRoom);

            /* JPA 관련 Hibernate 에러
              ## Error
              : object references an unsaved transient instance - save the transient instance before flushing
              ## 이유?
              : FK 로 사용되는 ChatRoom의 DB 컬럼값이 없는 상태에서 데이터를 넣으려다 발생한 에러이다.
              ## 해결방법?
              : 연관 관계 매핑해줄 때 사용하는 @ManyToOne, @OneToOne, @OneToMany 어노테이션에 cascade 옵션을 설정해준다.
              cascade 는 "영속성 전이" 라고 하는 개념인데 특정 엔티티를 영속화 할 때 연관된 엔티티도 함께 영속화 한다.
              저장할 때만 사용하려면 cascade = CascadeType.PERSIST 로 설정해주면 되며, 전체 적용인 CascadeType.ALL 로 설정하여 해결한다.
              joinChatRoom Entity => @ManyToOne(cascade = CascadeType.ALL)
                                     private ChatRoom chatRoom;
             */
            joinChatRoomRepository.save(joinChatRoomUserTwo);
            joinChatRoomRepository.save(joinChatRoomOtherUserTwo);

            List<JoinChatRoom> addJoinChatRoomList = new ArrayList<>();
            addJoinChatRoomList.add(joinChatRoomUserTwo);
            addJoinChatRoomList.add(joinChatRoomOtherUserTwo);
            chatRoom.addJoinChatRooms(addJoinChatRoomList);

            chatRoomRepository.save(chatRoom);

            chatRoomId = chatRoom.getChatRoomId();
        }
        return chatRoomId;
    }

}








