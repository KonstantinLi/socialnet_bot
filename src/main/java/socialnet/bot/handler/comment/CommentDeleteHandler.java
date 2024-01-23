package socialnet.bot.handler.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.HttpService;
import socialnet.bot.service.MessageService;
import socialnet.bot.service.TelegramService;

import static socialnet.bot.constant.Comment.COMMENT_DELETE;

@Component
@RequiredArgsConstructor
public class CommentDeleteHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final MessageService messageService;
    private final TelegramService telegramService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isCallbackStartsWith(request.getUpdate(), COMMENT_DELETE.getCommand());
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();
        Long postId = request.getCommentSession().getPostId();

        if (postId == null) {
            return;
        }

        Long commentId = messageService.getIdFromCallback(request, COMMENT_DELETE.getCommand());
        httpService.deleteComment(request, postId, commentId);

        telegramService.sendMessage(chatId, "<b>Комментарий удален</b>");
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
