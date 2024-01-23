package socialnet.bot.handler.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import socialnet.bot.dto.enums.state.FilterState;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.session.FilterSession;
import socialnet.bot.exception.IllegalFilterException;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.FilterService;
import socialnet.bot.service.KeyboardService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.FilterSessionService;

@Component
@RequiredArgsConstructor
public class FilterEnterHandler extends UserRequestHandler {
    private final FilterService filterService;
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;
    private final FilterSessionService filterSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        FilterState filterState = request.getFilterSession().getFilterState();
        return filterState != null &&
                filterState != FilterState.FILTERED &&
                isTextMessage(request.getUpdate());
    }

    @Override
    public void handle(SessionRq request) throws Exception {
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
