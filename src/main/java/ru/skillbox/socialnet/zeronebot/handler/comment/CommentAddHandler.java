package ru.skillbox.socialnet.zeronebot.handler.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.CommentState;
import ru.skillbox.socialnet.zeronebot.dto.request.CommentRq;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.session.CommentSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.MessageService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.CommentSessionService;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Comment.COMMENT_ADD;
import static ru.skillbox.socialnet.zeronebot.constant.Comment.COMMENT_COMMENT_ADD;

@Component
@RequiredArgsConstructor
public class CommentAddHandler extends UserRequestHandler {
    private final MessageService messageService;
    private final TelegramService telegramService;
    private final CommentSessionService commentSessionService;

    @Override
    public boolean isApplicable(UserRq request) {
        Update update = request.getUpdate();
        return isCallbackStartsWith(update, COMMENT_ADD.getCommand()) ||
                isCallbackStartsWith(update, COMMENT_COMMENT_ADD.getCommand());
    }

    @Override
    public void handle(UserRq request) throws IOException {
        Long chatId = request.getChatId();
        Update update = request.getUpdate();

        CommentSession commentSession = request.getCommentSession();

        Long parentId = isCallbackStartsWith(update, COMMENT_COMMENT_ADD.getCommand()) ?
                messageService.getIdFromCallback(request, COMMENT_COMMENT_ADD.getCommand()) :
                null;
        Long postId = isCallbackStartsWith(update, COMMENT_ADD.getCommand()) ?
                messageService.getIdFromCallback(request, COMMENT_ADD.getCommand()) :
                null;

        CommentRq commentRq = new CommentRq();
        commentRq.setIsDeleted(false);
        if (parentId != null) {
            commentRq.setParentId(parentId);
        }
        if (postId != null) {
            commentSession.setPostId(postId);
        }

        commentSession.setCommentState(CommentState.ADDING);
        commentSession.setComment(commentRq);
        commentSessionService.saveSession(chatId, commentSession);

        telegramService.sendMessage(chatId, "Напишите комментарий:");
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
