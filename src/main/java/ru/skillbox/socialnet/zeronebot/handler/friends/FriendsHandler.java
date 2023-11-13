package ru.skillbox.socialnet.zeronebot.handler.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Profile.FRIENDS;

@Component
@RequiredArgsConstructor
public class FriendsHandler extends UserRequestHandler {
    private final TelegramService telegramService;
    private final KeyboardService keyboardService;

    @Override
    public boolean isApplicable(UserRq request) {
        return isTextMessage(request.getUpdate(), FRIENDS);
    }

    @Override
    public void handle(UserRq request) throws IOException {
        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardService.buildFriendsMenu();
        telegramService.sendMessage(
                request.getChatId(),
                "Вкладка \"Друзья\"",
                replyKeyboardMarkup);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
