package com.partyluck.party_luck.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.partyluck.party_luck.websocket.dto.reponse.AlarmPageResponseDto;
import com.partyluck.party_luck.websocket.dto.reponse.EQMessageDto;
import com.partyluck.party_luck.websocket.dto.reponse.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {
    private final RedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        System.out.println("보이나 안보이나");
        String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
        System.out.println(publishMessage);
        if(publishMessage.split("\"")[1].equals("alarms")){
            try {
                AlarmPageResponseDto dto=objectMapper.readValue(publishMessage,AlarmPageResponseDto.class);
                System.out.println("alarms:"+dto.getAlarms());
                messagingTemplate.convertAndSend("/alarm/"+dto.getUserId().toString(),dto);
                System.out.println("성공");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        if(publishMessage.split("\"")[1].equals("message")){
            MessageResponseDto dto= null;
            try {
                dto = objectMapper.readValue(publishMessage, MessageResponseDto.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            messagingTemplate.convertAndSend("/queue/" + dto.getChatroomId(), dto);
        }
        if(publishMessage.split("\"")[1].equals("eqMessage")){
            EQMessageDto dto= null;
            try {
                dto = objectMapper.readValue(publishMessage, EQMessageDto.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            messagingTemplate.convertAndSend("/queue"+dto.getChatroomId(),dto.getEqMessage());

        }

    }
}
