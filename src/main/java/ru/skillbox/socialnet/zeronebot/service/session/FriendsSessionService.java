package ru.skillbox.socialnet.zeronebot.service.session;

import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.session.FriendsSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FriendsSessionService {
    private final Map<Long, FriendsSession> friendsSessionMap = new ConcurrentHashMap<>();

    public FriendsSession getSession(Long chatId) {
        return friendsSessionMap.getOrDefault(chatId,
                FriendsSession.builder().chatId(chatId).build());
    }

    public FriendsSession saveSession(Long chatId, FriendsSession session) {
        return friendsSessionMap.put(chatId, session);
    }

    public void deleteSession(Long chatId) {
        friendsSessionMap.remove(chatId);
    }
}
