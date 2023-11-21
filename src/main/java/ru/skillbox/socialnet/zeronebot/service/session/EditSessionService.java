package ru.skillbox.socialnet.zeronebot.service.session;

import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.session.EditSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EditSessionService {
    private final Map<Long, EditSession> editSessionMap = new ConcurrentHashMap<>();

    public EditSession getSession(Long chatId) {
        return editSessionMap.getOrDefault(chatId,
                EditSession.builder().chatId(chatId).build());
    }

    public EditSession saveSession(Long chatId, EditSession session) {
        return editSessionMap.put(chatId, session);
    }

    public void deleteSession(Long chatId) {
        editSessionMap.remove(chatId);
    }
}
