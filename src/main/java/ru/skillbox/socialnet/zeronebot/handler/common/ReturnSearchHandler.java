package ru.skillbox.socialnet.zeronebot.handler.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.FriendsSessionService;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Friends.SEARCH_EXIT;

@Component
@RequiredArgsConstructor
public class ReturnSearchHandler extends UserRequestHandler {
    private final TelegramService telegramService;
    private final FriendsSessionService friendsSessionService;

    private final KeyboardService keyboardService;

    @Override
    public boolean isApplicable(UserRq request) {
        return isTextMessage(request.getUpdate(), SEARCH_EXIT);
    }

    @Override
    public void handle(UserRq request) throws IOException {
        Long chatId = request.getChatId();

        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardService.buildFriendsMenu();
        telegramService.sendMessage(
                chatId,
                "Вы вернулись во вкладку <b>\"Друзья\"</b>",
                replyKeyboardMarkup);

        friendsSessionService.deleteSession(chatId);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
