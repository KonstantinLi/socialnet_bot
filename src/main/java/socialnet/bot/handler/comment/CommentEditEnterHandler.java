package socialnet.bot.handler.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import socialnet.bot.dto.enums.state.CommentState;
import socialnet.bot.dto.request.CommentRq;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.session.CommentSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.HttpService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.CommentSessionService;

@Component
@RequiredArgsConstructor
public class CommentEditEnterHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final TelegramService telegramService;
    private final CommentSessionService commentSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isTextMessage(request.getUpdate()) &&
            request.getCommentSession().getCommentState() == CommentState.EDITING;
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();
        String text = request.getUpdate().getMessage().getText();

        CommentSession commentSession = request.getCommentSession();
        CommentRq commentRq = commentSession.getComment();

        Long postId = commentSession.getPostId();
        Long commentId = commentRq.getId();

        commentRq.setId(null);
        commentRq.setCommentText(text);

        httpService.editComment(request, postId, commentId, commentRq);

        commentSession.setCommentState(null);
        commentSession.setComment(null);
        commentSessionService.saveSession(chatId, commentSession);

        telegramService.sendMessage(chatId, "<b>Комментарий отредактирован</b>");
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
