package ru.skillbox.socialnet.zeronebot.handler.like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.skillbox.socialnet.zeronebot.dto.enums.LikeType;
import ru.skillbox.socialnet.zeronebot.dto.request.LikeRq;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.response.CommentRs;
import ru.skillbox.socialnet.zeronebot.dto.response.PostRs;
import ru.skillbox.socialnet.zeronebot.dto.session.CommentSession;
import ru.skillbox.socialnet.zeronebot.dto.session.PostSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.CommentService;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.MessageService;
import ru.skillbox.socialnet.zeronebot.service.PostService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static ru.skillbox.socialnet.zeronebot.constant.Like.LIKE_COMMENT;
import static ru.skillbox.socialnet.zeronebot.constant.Like.UNLIKE_COMMENT;

@Component
@RequiredArgsConstructor
public class LikeCommentHandler extends UserRequestHandler {
    private final PostService postService;
    private final HttpService httpService;
    private final CommentService commentService;
    private final MessageService messageService;

    @Override
    public boolean isApplicable(UserRq request) {
        return isCallbackStartsWith(request.getUpdate(), LIKE_COMMENT.getCommand()) ||
                isCallbackStartsWith(request.getUpdate(), UNLIKE_COMMENT.getCommand());
    }

    @Override
    public void handle(UserRq request) throws IOException {
        Update update = request.getUpdate();

        PostSession postSession = request.getPostSession();
        CommentSession commentSession = request.getCommentSession();

        Long postId = commentSession.getPostId();
        Long parentId = commentSession.getParentId();

        if (postId == null) {
            return;
        }

        PostRs post = postService.getPostById(postSession.getPosts(), postId);
        List<CommentRs> comments = post.getComments();
        int size;

        if (parentId == null) {
            size = comments.size();
        } else {
            CommentRs parentComment = commentService.getCommentById(post.getComments(), parentId);
            size = parentComment.getSubComments().size();
        }

        String command = isCallbackStartsWith(update, LIKE_COMMENT.getCommand()) ?
                LIKE_COMMENT.getCommand() :
                UNLIKE_COMMENT.getCommand();

        Long commentId = messageService.getIdFromCallback(request, command);

        LikeRq likeRq = LikeRq.builder()
                .type(LikeType.Comment)
                .itemId(commentId)
                .build();

        if (isCallbackStartsWith(update, LIKE_COMMENT.getCommand())) {
            httpService.like(request, likeRq);
        } else {
            httpService.unlike(request, likeRq);
        }

        commentService.navigateComment(request, size);

        int index = Optional.ofNullable(commentSession.getIndex()).orElse(0);
        CommentRs comment = comments.get(index);

        commentService.sendCommentDetailsNavigate(request, comment);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
