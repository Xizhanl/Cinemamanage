package ljc.movie_manage.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ljc.movie_manage.entity.Sessions;
import ljc.movie_manage.service.ISessionsService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class SeatWebSocketHandler extends TextWebSocketHandler {

    private static final Map<Integer, CopyOnWriteArraySet<WebSocketSession>> sessionMap = new ConcurrentHashMap<>();//大房间表，存储用户实时状态，key：id，vale，当前用户
    private final ISessionsService sessionsService; //场次类
    private final ObjectMapper objectMapper;//把java对象转换成json发送

    public SeatWebSocketHandler(ISessionsService sessionsService, ObjectMapper objectMapper) {
        this.sessionsService = sessionsService;
        this.objectMapper = objectMapper;
    }
    //用户连接
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Integer sessionId = extractSessionId(session);//提取场次id
        if (sessionId != null) {
            sessionMap.computeIfAbsent(sessionId, k -> new CopyOnWriteArraySet<>()).add(session);//如果没有则新建一个
            sendInitialSeatStatus(session, sessionId);
        }
    }
    //选定座位，立即监听
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Integer sessionId = extractSessionId(session);
        if (sessionId == null) return;

        try {
            Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
            String type = (String) payload.get("type");

            switch (type) {
                case "lock":
                    handleSeatLock(sessionId, payload);
                    break;
                case "unlock":
                    handleSeatUnlock(sessionId, payload);
                    break;
                case "confirm":
                    handleSeatConfirm(sessionId, payload);
                    break;
            }
        } catch (JsonProcessingException e) {
            session.sendMessage(new TextMessage("{\"error\":\"Invalid message format\"}"));
        }
    }
    //用户断开连接
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Integer sessionId = extractSessionId(session);
        if (sessionId != null) {
            CopyOnWriteArraySet<WebSocketSession> sessions = sessionMap.get(sessionId);//清除用户
            if (sessions != null) {//如果没人，则清出场次
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    sessionMap.remove(sessionId);
                }
            }
        }
    }

    private Integer extractSessionId(WebSocketSession session) {
        try {
            String path = session.getUri().getPath();
            String[] parts = path.split("/");
            return Integer.parseInt(parts[2]); // 取第三部分
        } catch (Exception e) {
            return null;
        }
    }
    // 发送消息
    private void sendInitialSeatStatus(WebSocketSession session, Integer sessionId) throws IOException {
        Sessions sessionData = sessionsService.getById(sessionId);
        if (sessionData != null) {
            Map<String, Object> response = Map.of(
                "type", "init",
                "seatstatus", sessionData.getSeatstatus(),
                "seatsold", sessionData.getSeatssold()
            );
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
        }
    }

    private void handleSeatLock(Integer sessionId, Map<String, Object> payload) {
        // 实现座位锁定逻辑
        broadcastUpdate(sessionId, Map.of(
            "type", "lock",
            "row", payload.get("row"),
            "col", payload.get("col"),
            "userId", payload.get("userId"),
            "status", "locked"
        ));
    }

    private void handleSeatUnlock(Integer sessionId, Map<String, Object> payload) {
        // 实现座位解锁逻辑
        broadcastUpdate(sessionId, Map.of(
            "type", "unlock",
            "row", payload.get("row"),
            "col", payload.get("col"),
            "status", "available"
        ));
    }

    private void handleSeatConfirm(Integer sessionId, Map<String, Object> payload) {
        // 实现座位确认逻辑
        broadcastUpdate(sessionId, Map.of(
            "type", "confirm",
            "row", payload.get("row"),
            "col", payload.get("col"),
            "status", "sold"
        ));
    }

    public static void broadcastUpdate(Integer sessionId, Map<String, Object> update) {
        CopyOnWriteArraySet<WebSocketSession> sessions = sessionMap.get(sessionId);
        if (sessions != null) {
            String message;
            try {
                message = new ObjectMapper().writeValueAsString(update);
                TextMessage textMessage = new TextMessage(message);
                for (WebSocketSession session : sessions) {
                    if (session.isOpen()) {
                        try {
                            session.sendMessage(textMessage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
}