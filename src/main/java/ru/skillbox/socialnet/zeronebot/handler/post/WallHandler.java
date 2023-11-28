package ru.skillbox.socialnet.zeronebot.handler.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.PostState;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PostRs;
import ru.skillbox.socialnet.zeronebot.dto.session.PostSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.MessageService;
import ru.skillbox.socialnet.zeronebot.service.PostService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.CommentSessionService;
import ru.skillbox.socialnet.zeronebot.service.session.PostSessionService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static ru.skillbox.socialnet.zeronebot.constant.Navigate.NEXT_USER_POST;
import static ru.skillbox.socialnet.zeronebot.constant.Navigate.PREV_USER_POST;
import static ru.skillbox.socialnet.zeronebot.constant.Post.WALL;

@Component
@RequiredArgsConstructor
public class WallHandler extends UserRequestHandler {
    private final PostService postService;
    private final MessageService messageService;
    private final TelegramService telegramService;
    private final PostSessionService postSessionService;
    private final CommentSessionService commentSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        Update update = request.getUpdate();

        return isCallbackStartsWith(update, WALL.getCommand()) ||
                isCallbackStartsWith(update, PREV_USER_POST) ||
                isCallbackStartsWith(update, NEXT_USER_POST);
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();
        Update update = request.getUpdate();
        PostSession postSession = request.getPostSession();

        String command = isCallbackStartsWith(update, WALL.getCommand()) ?
                WALL.getCommand() :
                (isCallbackStartsWith(update, PREV_USER_POST) ?
                        PREV_USER_POST :
                        NEXT_USER_POST);

        Long authorId = messageService.getIdFromCallback(request, command);

        if (command.equals(WALL.getCommand())) {
            postService.wall(request, authorId);
            postSession.setPostState(PostState.WALL);
            postSession.setAuthorId(authorId);
            postSessionService.saveSession(chatId, postSession);
            commentSessionService.deleteSession(chatId);
        } else {
            postService.navigatePost(request, authorId);
        }

        List<PostRs> posts = postSession.getPosts();

        if (posts == null || posts.isEmpty()) {
            if (command.equals(WALL.getCommand())) {
                telegramService.sendMessage(chatId, "Публикации отсутствуют");
            }
            return;
        }

        int index = Optional.ofNullable(postSession.getIndex()).orElse(0);
        PostRs post = posts.get(index);

        postService.sendPostDetailsNavigate(request, post, true);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
