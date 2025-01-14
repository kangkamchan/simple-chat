package net.chat.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.chat.websocket.WebSocketMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
@RequiredArgsConstructor
@Log4j2
@Getter
@Component
public class ChatRoom {
    private final Map<String, WebSocketSession> chatRoomUsers = new ConcurrentHashMap<String, WebSocketSession>();
    private final ObjectMapper mapper;

    public void enter(ChatDTO chatDTO, WebSocketSession session) {
        String memberId = (String) session.getAttributes().get("memberId");
        if (memberId == null) {
            log.error("로그인 정보가 없습니다.");
            return;
        }
        chatRoomUsers.put(memberId, session);
        broadcastMessage(chatDTO, memberId, ChatMessageType.ENTER);
    }

    public void exit(ChatDTO chatDTO, String memberId){
        try {
            chatRoomUsers.remove(memberId).close();
            broadcastMessage(chatDTO, memberId, ChatMessageType.EXIT);
        }catch(IOException e) {
            log.error("IOException : {}",e.getMessage());
        }
    }

    public void sendMessage(ChatDTO chatDTO, String memberId){
        broadcastMessage(chatDTO, memberId, ChatMessageType.TALK);
    }

    /**
     * 채팅방의 모든 사용자에게 메시지를 전송함
     * @param chatDTO
     * @param sender : 보내는 사람 아이디, 본인에게 보내지 않는 기능 필요시 사용
     * @param type
     */
    private void broadcastMessage(ChatDTO chatDTO, String sender, ChatMessageType type){
        Set<String> userIds = chatRoomUsers.keySet();
        userIds.forEach((userId) -> {
            try {
                chatRoomUsers.get(userId).sendMessage(
                        getTextMessage(type,chatDTO)
                );
            } catch (IOException e) {
                log.error("Failed to send message to user {}: {}", userId, e.getMessage());
            }
        });
    }

    public TextMessage getTextMessage(ChatMessageType type, ChatDTO chatDTO) {
        try{
            return new TextMessage(
                    mapper.writeValueAsString(
                            new WebSocketMessage<>(chatDTO, type)
                    )
            );
        }catch(JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
