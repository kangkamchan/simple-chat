package net.chat.websocket;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import net.chat.dto.ChatMessageType;
@Getter
public class WebSocketMessage<T>{
    private final T payload;
    private final ChatMessageType type;
    @JsonCreator
    public WebSocketMessage(
            @JsonProperty("payload") T payload,
            @JsonProperty("type") ChatMessageType type) {
        this.payload = payload;
        this.type = type;
    }
    public static <T> WebSocketMessage<T> of(T payload, ChatMessageType type) {
        return new WebSocketMessage<>(payload,type);
    }
}
