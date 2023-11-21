package ru.skillbox.socialnet.zeronebot.service.session;

import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.session.UserSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserSessionService {
    private final Map<Long, UserSession> userSessionMap = new ConcurrentHashMap<>();

    public UserSession getSession(Long chatId) {
        return userSessionMap.getOrDefault(chatId,
                UserSession.builder().chatId(chatId).build());
    }

    public UserSession saveSession(Long chatId, UserSession session) {
        return userSessionMap.put(chatId, session);
    }

    public void deleteSession(Long chatId) {
        userSessionMap.remove(chatId);
    }
}
