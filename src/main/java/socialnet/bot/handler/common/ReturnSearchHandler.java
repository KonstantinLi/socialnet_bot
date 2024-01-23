package socialnet.bot.handler.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.KeyboardService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.FriendsSessionService;

import static socialnet.bot.constant.Friends.SEARCH_EXIT;

@Component
@RequiredArgsConstructor
public class ReturnSearchHandler extends UserRequestHandler {
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;
    private final FriendsSessionService friendsSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isTextMessage(request.getUpdate(), SEARCH_EXIT);
    }

    @Override
    public void handle(SessionRq request) throws Exception {
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
