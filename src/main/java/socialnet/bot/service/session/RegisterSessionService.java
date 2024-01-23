package socialnet.bot.service.session;

import org.springframework.stereotype.Component;
import socialnet.bot.dto.session.RegisterSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RegisterSessionService {
    private final Map<Long, RegisterSession> registerSessionMap = new ConcurrentHashMap<>();

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
