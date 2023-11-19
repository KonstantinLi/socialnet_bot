package ru.skillbox.socialnet.zeronebot.handler.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.request.CommentRq;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.session.CommentSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.CommentSessionService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CommentEditEnterHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final TelegramService telegramService;
    private final CommentSessionService commentSessionService;

    @Override
    public boolean isApplicable(UserRq request) {
        return isTextMessage(request.getUpdate()) &&
            request.getCommentSession().isEditing();
    }

    @Override
    public void handle(UserRq request) throws IOException {
        Long chatId = request.getChatId();
        String text = request.getUpdate().getMessage().getText();

        CommentSession commentSession = request.getCommentSession();
        CommentRq commentRq = commentSession.getComment();

        Long postId = commentSession.getPostId();
        Long commentId = commentRq.getId();

        commentRq.setId(null);
        commentRq.setCommentText(text);

        httpService.editComment(request, postId, commentId, commentRq);

        commentSession.setEditing(false);
        commentSession.setComment(null);
        commentSessionService.saveSession(chatId, commentSession);

        telegramService.sendMessage(chatId, "<b>Комментарий отредактирован</b>");
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
