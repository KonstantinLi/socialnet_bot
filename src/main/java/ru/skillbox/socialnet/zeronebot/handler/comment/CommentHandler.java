package ru.skillbox.socialnet.zeronebot.handler.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.response.CommentRs;
import ru.skillbox.socialnet.zeronebot.dto.response.PostRs;
import ru.skillbox.socialnet.zeronebot.dto.session.CommentSession;
import ru.skillbox.socialnet.zeronebot.dto.session.PostSession;
import ru.skillbox.socialnet.zeronebot.dto.session.UserSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.*;
import ru.skillbox.socialnet.zeronebot.service.session.CommentSessionService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static ru.skillbox.socialnet.zeronebot.constant.Comment.COMMENT;
import static ru.skillbox.socialnet.zeronebot.constant.Navigate.NEXT_COMMENT;
import static ru.skillbox.socialnet.zeronebot.constant.Navigate.PREV_COMMENT;

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
    public boolean isApplicable(UserRq request) {
        Update update = request.getUpdate();

        return isCallbackStartsWith(update, COMMENT.getCommand()) ||
                isCallback(update, PREV_COMMENT) ||
                isCallback(update, NEXT_COMMENT);
    }

    @Override
    public void handle(UserRq request) throws IOException {
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
