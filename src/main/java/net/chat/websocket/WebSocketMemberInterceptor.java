package net.chat.websocket;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
@Log4j2
@Component
@RequiredArgsConstructor
public class WebSocketMemberInterceptor implements HandshakeInterceptor {
    /**
     *  웹소켓 연결 전 인터셉터
     *  세션을 확인하여 로그인한 memberId가 없으면 상태코드 401을 반환
     *  로그인한 memberId가 있으면 속성에 저장함
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        if(request instanceof ServletServerHttpRequest) {
            HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
            HttpServletResponse res = ((ServletServerHttpResponse) response).getServletResponse();
            HttpSession session = req.getSession();
            if(session == null) {
                res.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }
            if(session.getAttribute("memberId") == null) {
                res.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }
            String memberId = (String) session.getAttribute("memberId");
            log.info("memberId:{}",memberId);
            attributes.put("memberId", memberId);
            return true;
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        log.info("WebSocket handshake completed");
    }
}
