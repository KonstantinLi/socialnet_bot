package ru.skillbox.socialnet.zeronebot.handler.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.request.CommentRq;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.session.CommentSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.MessageService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.CommentSessionService;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Comment.COMMENT_EDIT;

@Component
@RequiredArgsConstructor
public class CommentEditHandler extends UserRequestHandler {
    private final MessageService messageService;
    private final TelegramService telegramService;
    private final CommentSessionService commentSessionService;

    @Override
    public boolean isApplicable(UserRq request) {
        return isCallbackStartsWith(request.getUpdate(), COMMENT_EDIT.getCommand());
    }

    @Override
    public void handle(UserRq request) throws IOException {
        if (request.getCommentSession().getPostId() == null) {
            return;
        }

        Long chatId = request.getChatId();

        CommentSession commentSession = request.getCommentSession();
        Long parentId = commentSession.getParentId();
        Long commentId = messageService.getIdFromCallback(request, COMMENT_EDIT.getCommand());

        CommentRq commentRq = new CommentRq();
        commentRq.setId(commentId);
        commentRq.setIsDeleted(false);
        if (parentId != null) {
            commentRq.setParentId(parentId);
        }

        commentSession.setEditing(true);
        commentSession.setComment(commentRq);
        commentSessionService.saveSession(chatId, commentSession);

        telegramService.sendMessage(chatId, "Измените комментарий:");
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
