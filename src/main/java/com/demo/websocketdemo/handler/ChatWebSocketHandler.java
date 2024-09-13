package com.demo.websocketdemo.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserIdFromUrl(session);
        sessions.put(userId, session);
        log.info("sessions {}", sessions.keySet());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String userId = getUserIdFromUrl(session);
        log.info("userId {}", userId);
        String payload = message.getPayload();

        log.info("Message: {}", payload);


        if (payload.startsWith("broadcast:")) {
            String broadcastMessage = payload.substring(10);
            for (WebSocketSession s : sessions.values()) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(broadcastMessage));
                }
            }
        } else if (payload.startsWith("private:")) {
            String[] parts = payload.substring(8).split(":", 2);
            String targetUserId = parts[0];
            String privateMessage = parts[1];
            WebSocketSession targetSession = sessions.get(targetUserId);
            if (targetSession != null && targetSession.isOpen()) {
                targetSession.sendMessage(new TextMessage(privateMessage));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        sessions.remove(userId);
    }

    public static String getUserIdFromUrl(WebSocketSession session) {
        String userId = null;
        try {
            URI uri = new URI(session.getUri().toString());
            String query = uri.getQuery();
            if (query != null) {
                String[] params = query.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2 && "userId".equals(keyValue[0])) {
                        userId = keyValue[1];
                        break;
                    }
                }
            }
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
        }
        return userId;
    }
}
