package socialnet.bot.handler.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.response.CommentRs;
import socialnet.bot.dto.response.PostRs;
import socialnet.bot.dto.session.CommentSession;
import socialnet.bot.dto.session.PostSession;
import socialnet.bot.dto.session.UserSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.*;
import socialnet.bot.service.session.CommentSessionService;

import java.util.List;
import java.util.Optional;

import static socialnet.bot.constant.Comment.COMMENT;
import static socialnet.bot.constant.Navigate.NEXT_COMMENT;
import static socialnet.bot.constant.Navigate.PREV_COMMENT;

@Component
@RequiredArgsConstructor
public class CommentHandler extends UserRequestHandler {
    private final PostService postService;
    private final CommentService commentService;
    private final MessageService messageService;
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;
    private final CommentSessionService commentSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        Update update = request.getUpdate();

        return isCallbackStartsWith(update, COMMENT.getCommand()) ||
                isCallback(update, PREV_COMMENT) ||
                isCallback(update, NEXT_COMMENT);
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();
        Update update = request.getUpdate();

        CommentSession commentSession = request.getCommentSession();
        PostSession postSession = request.getPostSession();
        UserSession userSession = request.getUserSession();

        Long postId = isCallbackStartsWith(update, COMMENT.getCommand()) ?
                messageService.getIdFromCallback(request, COMMENT.getCommand()) :
                commentSession.getPostId();

        PostRs post = postService.getPostById(request, postSession.getPosts(), postId);

        List<CommentRs> comments = post.getComments()
                .stream()
                .sorted(commentService.commentComparator(userSession.getId()))
                .toList();

        InlineKeyboardMarkup markupInLine = keyboardService.buildPostCommentAddMenu(post);

        if (comments.isEmpty()) {
            telegramService.sendMessage(
                    chatId,
                    "<b>Комментарии отсутствуют</b>",
                    markupInLine);
            return;

        } else if (isCallbackStartsWith(update, COMMENT.getCommand())) {
            telegramService.sendMessage(
                    chatId,
                    "<b>Комментарии к посту</b>",
                    markupInLine);
            commentSession.setIndex(0);
            commentSession.setPostId(postId);

        } else {
            commentService.navigateComment(request, comments.size());
        }

        int index = Optional.ofNullable(commentSession.getIndex()).orElse(0);
        CommentRs comment = comments.get(index);

        commentService.sendCommentDetailsNavigate(request, comment);
        commentSessionService.saveSession(chatId, commentSession);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
