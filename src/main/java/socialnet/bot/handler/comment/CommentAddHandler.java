package socialnet.bot.handler.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import socialnet.bot.dto.enums.state.CommentState;
import socialnet.bot.dto.request.CommentRq;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.session.CommentSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.MessageService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.CommentSessionService;

import static socialnet.bot.constant.Comment.COMMENT_ADD;
import static socialnet.bot.constant.Comment.COMMENT_COMMENT_ADD;

@Component
@RequiredArgsConstructor
public class CommentAddHandler extends UserRequestHandler {
    private final MessageService messageService;
    private final TelegramService telegramService;
    private final CommentSessionService commentSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        Update update = request.getUpdate();
        return isCallbackStartsWith(update, COMMENT_ADD.getCommand()) ||
                isCallbackStartsWith(update, COMMENT_COMMENT_ADD.getCommand());
    }

    @Override
    public void handle(SessionRq request) throws Exception {
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
