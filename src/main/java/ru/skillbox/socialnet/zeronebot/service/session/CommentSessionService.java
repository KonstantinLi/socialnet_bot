package ru.skillbox.socialnet.zeronebot.service.session;

import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.session.CommentSession;

import java.util.HashMap;
import java.util.Map;

@Component
public class CommentSessionService {
    private final Map<Long, CommentSession> commentSessionMap = new HashMap<>();

    public CommentSession getSession(Long chatId) {
        return commentSessionMap.getOrDefault(chatId,
                CommentSession.builder().chatId(chatId).build());
    }

    public CommentSession saveSession(Long chatId, CommentSession session) {
        return commentSessionMap.put(chatId, session);
    }

    public void deleteSession(Long chatId) {
        commentSessionMap.remove(chatId);
    }
}
