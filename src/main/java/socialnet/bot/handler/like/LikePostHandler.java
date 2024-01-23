package socialnet.bot.handler.like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import socialnet.bot.dto.enums.LikeType;
import socialnet.bot.dto.request.LikeRq;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.response.PostRs;
import socialnet.bot.dto.session.PostSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.HttpService;
import socialnet.bot.service.MessageService;
import socialnet.bot.service.PostService;

import java.util.Optional;

import static socialnet.bot.constant.Like.LIKE_POST;
import static socialnet.bot.constant.Like.UNLIKE_POST;

@Component
@RequiredArgsConstructor
public class LikePostHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final PostService postService;
    private final MessageService messageService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isCallbackStartsWith(request.getUpdate(), LIKE_POST.getCommand()) ||
                isCallbackStartsWith(request.getUpdate(), UNLIKE_POST.getCommand());
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Update update = request.getUpdate();
        PostSession postSession = request.getPostSession();

        String command = isCallbackStartsWith(update, LIKE_POST.getCommand()) ?
                LIKE_POST.getCommand() :
                UNLIKE_POST.getCommand();

        Long postId = messageService.getIdFromCallback(request, command);

        LikeRq likeRq = LikeRq.builder()
                .type(LikeType.Post)
                .itemId(postId)
                .build();

        if (isCallbackStartsWith(update, LIKE_POST.getCommand())) {
            httpService.like(request, likeRq);
        } else {
            httpService.unlike(request, likeRq);
        }

        postService.navigatePost(request, postSession.getAuthorId());

        int index = Optional.ofNullable(postSession.getIndex()).orElse(0);
        PostRs post = postSession.getPosts().get(index);

        postService.sendPostDetailsNavigate(request, post, false);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
