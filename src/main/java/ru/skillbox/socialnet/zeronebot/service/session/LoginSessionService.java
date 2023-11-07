package ru.skillbox.socialnet.zeronebot.service.session;

import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.session.LoginSession;

import java.util.HashMap;
import java.util.Map;

@Component
public class LoginSessionService {
    private final Map<Long, LoginSession> loginSessionMap = new HashMap<>();

    public LoginSession getSession(Long chatId) {
        return loginSessionMap.getOrDefault(chatId,
                LoginSession.builder().chatId(chatId).build());
    }

    public LoginSession saveSession(Long chatId, LoginSession session) {
        return loginSessionMap.put(chatId, session);
    }

    public void deleteSession(Long chatId) {
        loginSessionMap.remove(chatId);
    }
}
