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
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.*;
import socialnet.bot.service.session.CommentSessionService;

import java.util.List;
import java.util.Optional;

import static socialnet.bot.constant.Comment.COMMENT_COMMENT;
import static socialnet.bot.constant.Navigate.NEXT_COMMENT_COMMENT;
import static socialnet.bot.constant.Navigate.PREV_COMMENT_COMMENT;

@Component
@RequiredArgsConstructor
public class CommentCommentHandler extends UserRequestHandler {
    private final PostService postService;
    private final CommentService commentService;
    private final MessageService messageService;
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;
    private final CommentSessionService commentSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        Update update = request.getUpdate();

        return isCallbackStartsWith(update, COMMENT_COMMENT.getCommand()) ||
                isCallback(update, PREV_COMMENT_COMMENT) ||
                isCallback(update, NEXT_COMMENT_COMMENT);
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long id = request.getUserSession().getId();
        Long chatId = request.getChatId();
        Update update = request.getUpdate();

        CommentSession commentSession = request.getCommentSession();
        PostSession postSession = request.getPostSession();

        Long postId = commentSession.getPostId();
        Long parentId = isCallbackStartsWith(update, COMMENT_COMMENT.getCommand()) ?
                messageService.getIdFromCallback(request, COMMENT_COMMENT.getCommand()) :
                commentSession.getParentId();

        PostRs post = postService.getPostById(request, postSession.getPosts(), postId);
        CommentRs parentComment = commentService.getCommentById(post.getComments(), parentId);

        List<CommentRs> comments = parentComment.getSubComments()
                .stream()
                .sorted(commentService.commentComparator(id))
                .toList();

        InlineKeyboardMarkup markupInLine = keyboardService.buildCommentCommentAddMenu(parentComment);

        if (comments.isEmpty()) {
            telegramService.sendMessage(
                    chatId,
                    "<b>Вложенные комментарии отсутствуют</b>",
                    markupInLine);
            return;

        } else if (isCallbackStartsWith(update, COMMENT_COMMENT.getCommand())) {
            telegramService.sendMessage(
                    chatId,
                    "<b>Вложенные комментарии</b>",
                    markupInLine);
            commentSession.setSubIndex(0);
            commentSession.setParentId(parentId);

        } else {
            commentService.navigateCommentComment(request, comments.size());
        }

        int index = Optional.ofNullable(commentSession.getSubIndex()).orElse(0);
        CommentRs comment = comments.get(index);

        commentService.sendCommentCommentDetailsNavigate(request, comment);
        commentSessionService.saveSession(chatId, commentSession);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
