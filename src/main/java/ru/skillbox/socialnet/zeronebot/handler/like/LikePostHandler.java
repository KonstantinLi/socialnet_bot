package ru.skillbox.socialnet.zeronebot.handler.like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.skillbox.socialnet.zeronebot.dto.enums.LikeType;
import ru.skillbox.socialnet.zeronebot.dto.request.LikeRq;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PostRs;
import ru.skillbox.socialnet.zeronebot.dto.session.PostSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.MessageService;
import ru.skillbox.socialnet.zeronebot.service.PostService;

import java.io.IOException;
import java.util.Optional;

import static ru.skillbox.socialnet.zeronebot.constant.Like.LIKE_POST;
import static ru.skillbox.socialnet.zeronebot.constant.Like.UNLIKE_POST;

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
