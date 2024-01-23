package socialnet.bot.handler.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.session.FilterSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.KeyboardService;
import socialnet.bot.service.TelegramService;

import static socialnet.bot.constant.Friends.FRIENDS_SEARCH;

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
