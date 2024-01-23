package socialnet.bot.service.session;

import org.springframework.stereotype.Component;
import socialnet.bot.dto.session.LoginSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginSessionService {
    private final Map<Long, LoginSession> loginSessionMap = new ConcurrentHashMap<>();

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
