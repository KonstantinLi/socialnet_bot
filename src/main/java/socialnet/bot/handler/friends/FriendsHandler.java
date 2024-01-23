package socialnet.bot.handler.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.KeyboardService;
import socialnet.bot.service.TelegramService;

import static socialnet.bot.constant.Menu.FRIENDS;

@Component
@RequiredArgsConstructor
public class FriendsHandler extends UserRequestHandler {
    private final TelegramService telegramService;
    private final KeyboardService keyboardService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isCommand(request.getUpdate(), FRIENDS.getCommand());
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardService.buildFriendsMenu();
        telegramService.sendMessage(
                request.getChatId(),
                "Вкладка <b>\"Друзья\"</b>",
                replyKeyboardMarkup);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
