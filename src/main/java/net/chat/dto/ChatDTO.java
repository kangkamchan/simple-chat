package net.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
public class ChatDTO {
    private String sender;
    private String message;
    private int chatRoomId;
    private LocalDateTime sentAt;

    private final String ENTER_MESSAGE = "님이 입장하셨습니다.";
    private final String EXIT_MESSAGE = "님이 퇴장하셨습니다.";
}
