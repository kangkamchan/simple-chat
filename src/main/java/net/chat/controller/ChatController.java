package net.chat.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/chat")
public class ChatController {
    @PostMapping("/chatRoom")
    public String chatRoom(String memberId, HttpSession session) {
        session.setAttribute("memberId", memberId);
        return "chat/chatRoom";
    }
    @GetMapping("/{chat-room-id}")
    public String chat(@PathVariable(value="chat-room-id") int chatRoomId, Model model) {
        model.addAttribute("chatRoomId", chatRoomId);
        return "chat/chat";
    }
}
