package ru.skillbox.socialnet.zeronebot.handler.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.PostState;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.dto.session.PostSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.PostSessionService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class PostTextHandler extends UserRequestHandler {
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;
    private final PostSessionService postSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        PostState postState = request.getPostSession().getPostState();
        return isTextMessage(request.getUpdate()) && postState == PostState.TEXT_WAIT;
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();
        PostSession postSession = request.getPostSession();

        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardService.buildTagsMenu();
        telegramService.sendMessage(
                chatId,
                "Теперь введите теги:",
                replyKeyboardMarkup);

        String text = request.getUpdate().getMessage().getText();

        postSession.getPublish().setPostText(text);
        postSession.setPostState(PostState.TAGS_WAIT);
        postSessionService.saveSession(chatId, postSession);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
