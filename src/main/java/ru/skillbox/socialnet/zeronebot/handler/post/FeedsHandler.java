package ru.skillbox.socialnet.zeronebot.handler.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import ru.skillbox.socialnet.zeronebot.constant.Menu;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.PostState;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PostRs;
import ru.skillbox.socialnet.zeronebot.dto.session.PostSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.PostService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.CommentSessionService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static ru.skillbox.socialnet.zeronebot.constant.Navigate.NEXT_POST;
import static ru.skillbox.socialnet.zeronebot.constant.Navigate.PREV_POST;

@Component
@RequiredArgsConstructor
public class FeedsHandler extends UserRequestHandler {
    private final PostService postService;
    private final TelegramService telegramService;
    private final CommentSessionService commentSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        Update update  = request.getUpdate();

        return isCommand(update, Menu.NEWS.getCommand()) ||
                isCallback(update, PREV_POST) ||
                isCallback(update, NEXT_POST);
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();
        Update update = request.getUpdate();
        PostSession postSession = request.getPostSession();

        if (isCommand(update, Menu.NEWS.getCommand())) {
            postService.feeds(request);

            ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
            keyboardRemove.setRemoveKeyboard(true);
            telegramService.sendMessage(
                    chatId,
                    "Вкладка <b>\"Новости\"</b>",
                    keyboardRemove);

            postSession.setPostState(PostState.FEEDS);
            commentSessionService.deleteSession(chatId);
        } else {
            postService.navigatePost(request, null);
        }

        List<PostRs> posts = postSession.getPosts();

        if (posts == null || posts.isEmpty()) {
            return;
        }

        int index = Optional.ofNullable(postSession.getIndex()).orElse(0);
        PostRs post = posts.get(index);

        postService.sendPostDetailsNavigate(request, post, false);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
