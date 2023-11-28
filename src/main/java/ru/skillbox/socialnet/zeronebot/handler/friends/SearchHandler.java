package ru.skillbox.socialnet.zeronebot.handler.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.dto.session.FilterSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Friends.FRIENDS_SEARCH;

@Component
@RequiredArgsConstructor
public class SearchHandler extends UserRequestHandler {
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isTextMessage(request.getUpdate(), FRIENDS_SEARCH);
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        FilterSession filterSession = request.getFilterSession();

        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardService.buildFilterMenu(filterSession);
        telegramService.sendMessage(
                request.getChatId(),
                "Вкладка <b>\"Глобальный поиск\"</b>",
                replyKeyboardMarkup);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
