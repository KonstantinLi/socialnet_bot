package ru.skillbox.socialnet.zeronebot.handler.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.FilterState;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.session.FilterSession;
import ru.skillbox.socialnet.zeronebot.exception.IllegalFilterException;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.FilterService;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.FilterSessionService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FilterEnterHandler extends UserRequestHandler {
    private final FilterService filterService;
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;
    private final FilterSessionService filterSessionService;

    @Override
    public boolean isApplicable(UserRq request) {
        FilterState filterState = request.getFilterSession().getFilterState();
        return filterState != null &&
                filterState != FilterState.FILTERED &&
                isTextMessage(request.getUpdate());
    }

    @Override
    public void handle(UserRq request) throws IOException {
        Long chatId = request.getChatId();
        String message = request.getUpdate().getMessage().getText();

        FilterSession filterSession = request.getFilterSession();
        FilterState filterState = filterSession.getFilterState();

        try {
            filterService.setFilterProperty(filterSession, filterState, message);

            ReplyKeyboardMarkup replyKeyboardMarkup = keyboardService.buildFilterMenu(filterSession);
            telegramService.sendMessage(
                    chatId,
                    "Фильтр успешно применен",
                    replyKeyboardMarkup);

        } catch (NumberFormatException ex) {
            throw new IllegalFilterException("Вы должны ввести число");

        } finally {
            filterSession.setFilterState(FilterState.FILTERED);
            filterSessionService.saveSession(chatId, filterSession);
        }
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
