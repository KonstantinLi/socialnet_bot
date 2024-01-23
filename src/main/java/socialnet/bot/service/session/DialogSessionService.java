package socialnet.bot.service.session;

import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import socialnet.bot.dto.session.DialogSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DialogSessionService {
    private final Map<Long, DialogSession> dialogSessionMap = new ConcurrentHashMap<>();

    public DialogSession getSession(Long chatId) {
        return dialogSessionMap.getOrDefault(chatId,
                DialogSession.builder().chatId(chatId).build());
    }

    public DialogSession saveSession(Long chatId, DialogSession session) {
        return dialogSessionMap.put(chatId, session);
    }

    public void deleteSession(Long chatId) {
        DialogSession dialogSession = dialogSessionMap.get(chatId);

        if (dialogSession != null) {
            StompSession stompSession = dialogSession.getStompSession();
            if (stompSession != null) {
                stompSession.disconnect();
            }
        }

        dialogSessionMap.remove(chatId);
    }
}
