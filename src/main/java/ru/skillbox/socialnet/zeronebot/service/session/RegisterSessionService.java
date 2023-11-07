package ru.skillbox.socialnet.zeronebot.service.session;

import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.session.RegisterSession;

import java.util.HashMap;
import java.util.Map;

@Component
public class RegisterSessionService {
    private final Map<Long, RegisterSession> registerSessionMap = new HashMap<>();

    public RegisterSession getSession(Long chatId) {
        return registerSessionMap.getOrDefault(chatId,
                RegisterSession.builder().chatId(chatId).build());
    }

    public RegisterSession saveSession(Long chatId, RegisterSession session) {
        return registerSessionMap.put(chatId, session);
    }

    public void deleteSession(Long chatId) {
        registerSessionMap.remove(chatId);
    }
}
