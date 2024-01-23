package socialnet.bot.handler.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import socialnet.bot.dto.enums.state.CommentState;
import socialnet.bot.dto.request.CommentRq;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.session.CommentSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.MessageService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.CommentSessionService;

import static socialnet.bot.constant.Comment.COMMENT_EDIT;

@Component
@RequiredArgsConstructor
public class CommentEditHandler extends UserRequestHandler {
    private final MessageService messageService;
    private final TelegramService telegramService;
    private final CommentSessionService commentSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isCallbackStartsWith(request.getUpdate(), COMMENT_EDIT.getCommand());
    }

    @Override
    public void handle(SessionRq request) throws Exception {
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

        commentSession.setCommentState(CommentState.EDITING);
        commentSession.setComment(commentRq);
        commentSessionService.saveSession(chatId, commentSession);

        telegramService.sendMessage(chatId, "Измените комментарий:");
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
