package ru.skillbox.socialnet.zeronebot.service.session;

import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.session.FilterSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FilterSessionService {
    private final Map<Long, FilterSession> filterSessionMap = new ConcurrentHashMap<>();

    public FilterSession getSession(Long chatId) {
        return filterSessionMap.getOrDefault(chatId,
                FilterSession.builder().chatId(chatId).build());
    }

    public FilterSession saveSession(Long chatId, FilterSession session) {
        return filterSessionMap.put(chatId, session);
    }

    public void deleteSession(Long chatId) {
        filterSessionMap.remove(chatId);
    }
}
