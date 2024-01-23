package socialnet.bot.handler.like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import socialnet.bot.dto.enums.LikeType;
import socialnet.bot.dto.request.LikeRq;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.response.CommentRs;
import socialnet.bot.dto.response.PostRs;
import socialnet.bot.dto.session.CommentSession;
import socialnet.bot.dto.session.PostSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.CommentService;
import socialnet.bot.service.HttpService;
import socialnet.bot.service.MessageService;
import socialnet.bot.service.PostService;

import java.util.List;
import java.util.Optional;

import static socialnet.bot.constant.Like.LIKE_COMMENT;
import static socialnet.bot.constant.Like.UNLIKE_COMMENT;

@Component
@RequiredArgsConstructor
public class LikeCommentHandler extends UserRequestHandler {
    private final PostService postService;
    private final HttpService httpService;
    private final CommentService commentService;
    private final MessageService messageService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isCallbackStartsWith(request.getUpdate(), LIKE_COMMENT.getCommand()) ||
                isCallbackStartsWith(request.getUpdate(), UNLIKE_COMMENT.getCommand());
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Update update = request.getUpdate();

        PostSession postSession = request.getPostSession();
        CommentSession commentSession = request.getCommentSession();

        Long postId = commentSession.getPostId();
        Long parentId = commentSession.getParentId();

        if (postId == null) {
            return;
        }

        PostRs post = postService.getPostById(request, postSession.getPosts(), postId);
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
