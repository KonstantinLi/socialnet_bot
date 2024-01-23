package socialnet.bot.handler.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import socialnet.bot.dto.enums.state.FilterState;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.session.FilterSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.KeyboardService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.FilterSessionService;

import static socialnet.bot.constant.Filter.DELETE;

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
