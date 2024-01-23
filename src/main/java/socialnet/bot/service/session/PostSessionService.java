package socialnet.bot.service.session;

import org.springframework.stereotype.Component;
import socialnet.bot.dto.session.PostSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PostSessionService {
    private final Map<Long, PostSession> postSessionMap = new ConcurrentHashMap<>();

    public PostSession getSession(Long chatId) {
        return postSessionMap.getOrDefault(chatId,
                PostSession.builder().chatId(chatId).build());
    }

    public PostSession saveSession(Long chatId, PostSession session) {
        return postSessionMap.put(chatId, session);
    }

    public void deleteSession(Long chatId) {
        postSessionMap.remove(chatId);
    }
}
