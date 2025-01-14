package net.chat.websocket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.chat.dto.ChatDTO;
import net.chat.dto.ChatMessageType;
import net.chat.dto.ChatRoom;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Log4j2
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {
    private static final ConcurrentHashMap<String, WebSocketSession> CHAT_ROOM_USER_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, ChatRoom> CHAT_ROOM_MAP = new ConcurrentHashMap<>();
    private final ObjectMapper mapper;
    private static int chatRoomId = 0;

    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        try  {
            String memberId = session.getAttributes().get("memberId").toString();
            CHAT_ROOM_USER_MAP.put(memberId, session);
            session.sendMessage(
                    new TextMessage("웹소켓 연결 성공")
            );
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        try{
            String memberId = session.getAttributes().get("memberId").toString();
            CHAT_ROOM_USER_MAP.remove(memberId);
        }catch(Exception e){
            log.error(e.getMessage());
        }
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String memberId = (String)session.getAttributes().get("memberId");
        WebSocketMessage<ChatDTO> websocketMessage = mapper.readValue(message.getPayload(), new TypeReference<WebSocketMessage<ChatDTO>>() {});

        switch(websocketMessage.getType().getType()){
            case "CREATE" -> createChatRoom(websocketMessage.getPayload(), session);
            case "INFO" -> chatRoomInfo(session);
            case "ENTER" -> enterChatRoom(websocketMessage.getPayload(), session);
            case "EXIT" -> exitChatRoom(websocketMessage.getPayload(), session);
            case "TALK" -> sendMessage(websocketMessage.getPayload(), session);
        }
    }

    private void createChatRoom(ChatDTO chatDTO, WebSocketSession session){
        log.info("createChatRoom : {}", chatDTO);
        CHAT_ROOM_MAP.put(++chatRoomId,new ChatRoom(mapper));
        CHAT_ROOM_USER_MAP.keySet().forEach((memberId)->{
            chatRoomInfo(CHAT_ROOM_USER_MAP.get(memberId));
        });
    }

    private void chatRoomInfo(WebSocketSession session){
        try {
            session.sendMessage(
                    new TextMessage(
                            mapper.writeValueAsString(
                                    new WebSocketMessage<>(CHAT_ROOM_MAP.keySet(), ChatMessageType.INFO)
                            )
                    )
            );
        }catch(IOException e){
            log.error(e.getMessage());
        }
    }

    private void enterChatRoom(ChatDTO chatDTO, WebSocketSession session){
        log.info("enterChatRoom : {}", chatDTO);
        chatDTO.setMessage(chatDTO.getSender() + chatDTO.getENTER_MESSAGE());
        ChatRoom chatRoom = CHAT_ROOM_MAP.getOrDefault(
                chatDTO.getChatRoomId(),
                new ChatRoom(mapper)
        );
        chatRoom.enter(chatDTO, session);
        CHAT_ROOM_MAP.put(chatDTO.getChatRoomId(), chatRoom);
    }

    private void exitChatRoom(ChatDTO chatDTO, WebSocketSession session){
        log.info("exitChatRoom : {}", chatDTO);
        chatDTO.setMessage(chatDTO.getSender() + chatDTO.getEXIT_MESSAGE());
        ChatRoom chatRoom = CHAT_ROOM_MAP.get(chatDTO.getChatRoomId());
        chatRoom.exit(chatDTO, chatDTO.getSender());
        if(chatRoom.getChatRoomUsers().isEmpty()){
            CHAT_ROOM_MAP.remove(chatDTO.getChatRoomId());
            CHAT_ROOM_USER_MAP.keySet().forEach((memberId)->{
                chatRoomInfo(CHAT_ROOM_USER_MAP.get(memberId));
            });
        }
    }

    private void sendMessage(ChatDTO chatDTO, WebSocketSession session){
        log.info("sendMessage : {}", chatDTO);
        ChatRoom chatRoom = CHAT_ROOM_MAP.get(chatDTO.getChatRoomId());
        chatRoom.sendMessage(chatDTO, chatDTO.getSender());
    }
}
