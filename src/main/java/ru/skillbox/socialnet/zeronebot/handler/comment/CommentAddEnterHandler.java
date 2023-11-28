package ru.skillbox.socialnet.zeronebot.handler.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.CommentState;
import ru.skillbox.socialnet.zeronebot.dto.request.CommentRq;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.dto.session.CommentSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.CommentSessionService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CommentAddEnterHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final TelegramService telegramService;
    private final CommentSessionService commentSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isTextMessage(request.getUpdate()) &&
                request.getCommentSession().getCommentState() == CommentState.ADDING;
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();
        String text = request.getUpdate().getMessage().getText();

        CommentSession commentSession = request.getCommentSession();
        Long postId = commentSession.getPostId();

        CommentRq commentRq = commentSession.getComment();
        commentRq.setCommentText(text);

        httpService.addComment(request, postId, commentRq);

        commentSession.setCommentState(null);
        commentSession.setComment(null);
        commentSessionService.saveSession(chatId, commentSession);

        telegramService.sendMessage(chatId, "<b>Комментарий добавлен</b>");
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
