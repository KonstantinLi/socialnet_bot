package ru.skillbox.socialnet.zeronebot.handler.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.FilterState;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.dto.session.FilterSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.FilterSessionService;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Filter.DELETE;

@Component
@RequiredArgsConstructor
public class FilterDeleteHandler extends UserRequestHandler {
    private final TelegramService telegramService;
    private final FilterSessionService filterSessionService;
    private final KeyboardService keyboardService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isTextMessage(request.getUpdate(), DELETE);
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();

        if (request.getFilterSession().getFilterState() != FilterState.FILTERED) {
            telegramService.sendMessage(
                    chatId,
                    "Фильтры отсутствуют");
            return;
        }

        filterSessionService.deleteSession(chatId);
        FilterSession filterSession = FilterSession.builder().build();

        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardService.buildFilterMenu(filterSession);
        telegramService.sendMessage(
                chatId,
                "Фильтры успешно удалены",
                replyKeyboardMarkup);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
