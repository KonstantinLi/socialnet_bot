package ru.skillbox.socialnet.zeronebot.service.session;

import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.session.DialogSession;

import java.util.HashMap;
import java.util.Map;

@Component
public class DialogSessionService {
    private final Map<Long, DialogSession> dialogSessionMap = new HashMap<>();

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
