package com.partyluck.party_luck.chatmessage;

import com.partyluck.party_luck.user.domain.User;
import com.partyluck.party_luck.user.repository.InitialInfoRepository;
import com.partyluck.party_luck.user.repository.UserRepository;
import com.partyluck.party_luck.security.UserDetailsImpl;
import com.partyluck.party_luck.security.jwt.JwtDecoder;
import com.partyluck.party_luck.chatroom.domain.ChatRoom;
import com.partyluck.party_luck.chatroom.domain.JoinChatRoom;
import com.partyluck.party_luck.chatmessage.responseDto.MessageResponseDto;
import com.partyluck.party_luck.chatmessage.requestDto.MessageRequestDto;
import com.partyluck.party_luck.chatroom.repository.ChatRoomRepository;
import com.partyluck.party_luck.chatroom.repository.JoinChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final JoinChatRoomRepository joinChatRoomRepository;
    private final JwtDecoder jwtDecoder;
    private final InitialInfoRepository initialInfoRepository;

    public MessageResponseDto save(MessageRequestDto message, String token) {
        String username = jwtDecoder.decodeUsername(token);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다.")
        );

        Long userId = user.getId();
        String chatRoomId = message.getChatRoomId();
        System.out.println(message.getChatRoomId());
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId).orElseThrow(
                () -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다.")
        );

        String msg = message.getMessage();
        ChatMessage.MessageType messageType = message.getType();
        // createAt 날짜 만들기 (아시아/서울 시간)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date now = new Date();
        String createdAt = sdf.format(now);

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .senderId(userId)
                .message(message)
                .createdAt(createdAt)
                .build();
        ChatMessage storedChatMessage = chatMessageRepository.save(chatMessage);
        chatRoom.addLastMessage(storedChatMessage.getMessageId());
        chatRoomRepository.save(chatRoom);

        return  new MessageResponseDto(message.getMessage(),extractDateFormat(createdAt),chatMessage.getSenderId(),initialInfoRepository.findInitialInfoByUserId(userId).orElse(null).getProfile_img(),message.getChatRoomId());
//        return MessageResponseDto.builder()
//                .message(message.getMessage())
//                .createdAt(extractDateFormat(createdAt))
//                .userId(chatMessage.getSenderId())
//                .imageUrl(initialInfoRepository.findInitialInfoByUserId(userId).orElse(null).getProfile_img())
//                .chatroomId(message.getChatRoomId())
//                .build();
    }

    // 해당 채팅방 메시지 조회
    public List<MessageResponseDto> readMessages(UserDetailsImpl userDetails, String chatroomId) {
        Long userId = userDetails.getUser().getId();

        // 해당방에 맞는 유저인지 검증
        List<JoinChatRoom> joinChatRoomList = joinChatRoomRepository.findJoinChatRoomsByChatRoom_ChatRoomId(chatroomId);
        checkCollectUser(userId, joinChatRoomList);

        List<ChatMessage> chatMessageList = chatMessageRepository.findChatMessagesByChatroom_ChatRoomIdOrderByCreatedAt(chatroomId);
        System.out.println("채팅 메시지 개수 : " + chatMessageList);

        List<MessageResponseDto> messageResponseDtoList = new ArrayList<>();

        for(ChatMessage chatMessage : chatMessageList) {
            Long senderId = chatMessage.getSenderId();
            User sender = userRepository.findById(senderId).orElse(null);
            if(sender==null){
                MessageResponseDto messageResponseDto = MessageResponseDto.builder()
                        .message(chatMessage.getMessage())
                        .createdAt(extractDateFormat(chatMessage.getCreatedAt()))
                        .userId(senderId)
                        .imageUrl(null)
                        .build();
                messageResponseDtoList.add(messageResponseDto);

            }
            else{
                MessageResponseDto messageResponseDto = MessageResponseDto.builder()
                        .message(chatMessage.getMessage())
                        .createdAt(extractDateFormat(chatMessage.getCreatedAt()))
                        .userId(senderId)
                        .imageUrl(initialInfoRepository.findInitialInfoByUserId(senderId).orElse(null).getProfile_img())
                        .build();
                messageResponseDtoList.add(messageResponseDto);
            }
        }
        return messageResponseDtoList;
    }


    // 해당방에 맞는 유저인지 검증하는 메서드
    private void checkCollectUser(Long userId, List<JoinChatRoom> joinChatRoomList) {
        int cnt=0;
        for(JoinChatRoom joinChatRoom : joinChatRoomList) {
            if(joinChatRoom.getUser().getId().equals(userId)) {
                break;
            } else {
                cnt++;
            }
        }
        if(cnt== joinChatRoomList.size())
            throw new IllegalArgumentException("해당 채팅방에 잘못된 유저가 접근하였습니다.");
    }

    // 채팅방 메시지 날짜&시간 형식을 만드는 메서드
    private String extractDateFormat(String messageDate) {

        String result = "";
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedNow = simpleDateFormat.format(now).toString();
        String[] curFormattedSplitByDash = formattedNow.split("-");
        String curYear = curFormattedSplitByDash[0];
        String curMonth = curFormattedSplitByDash[1];
        String curDay = curFormattedSplitByDash[2].split(" ")[0];
        String curHour = curFormattedSplitByDash[2].split(" ")[1].split(":")[0];
        String curMinute = curFormattedSplitByDash[2].split(" ")[1].split(":")[1];
        System.out.println("----------------- 현재 날짜 & 시각 -----------------");
        System.out.println(curYear + "년 " + curMonth + "월 " + curDay + "일 " + curHour + "시 " + curMinute + "분 ");

        String[] formattedSplitByDash = messageDate.split("-");
        String year = formattedSplitByDash[0];
        String month = formattedSplitByDash[1];
        String day = formattedSplitByDash[2].split(" ")[0];
        String hour = formattedSplitByDash[2].split(" ")[1].split(":")[0];
        String minute = formattedSplitByDash[2].split(" ")[1].split(":")[1];

        // 1) 오늘인지 아닌지
        if(curMonth.equals(month) && curDay.equals(day)) {
            // 2) 오전인지 오후 인지
            if(Integer.parseInt(hour) < 12) {
                result = "오전 " + hour + ":" + minute;
            } else {
                Integer afterHour = (Integer.parseInt(hour) - 12);
                result = "오후 " + afterHour.toString() + ":" + minute;
            }
        } else if (curMonth.equals(month) && ((Integer.parseInt(curDay) - 1) == Integer.parseInt(day))) {
            // 3) 하루 전인지 아닌지
            result = "하루 전";
        } else {
            // 4) 하루 전이 아니라면
            result = month + "월 " + day + "일";
        }
        return result;
    }
}
