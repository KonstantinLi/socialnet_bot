package socialnet.bot.handler.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import socialnet.bot.config.ZeroneProperties;
import socialnet.bot.dto.enums.state.SessionState;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.session.UserSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.KeyboardService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.UserSessionService;

import java.net.URL;

@Component
@RequiredArgsConstructor
public class StartCommandHandler extends UserRequestHandler {
    private final ZeroneProperties zeroneProperties;

    private final KeyboardService keyboardService;
    private final TelegramService telegramService;
    private final UserSessionService userSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isCommand(request.getUpdate(), "/start");
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();

        InlineKeyboardMarkup markupInLine = keyboardService.buildAuthMenu();

        try {
            telegramService.sendPhotoURL(
                    chatId,
                    new URL(zeroneProperties.getPhoto()),
                    zeroneProperties.getWelcome(),
                    markupInLine);
        } catch (Exception ex) {
            telegramService.sendMessage(
                    chatId,
                    zeroneProperties.getWelcome(),
                    markupInLine);
        }

        UserSession session = request.getUserSession();
        session.setSessionState(SessionState.UNAUTHORIZED);
        userSessionService.saveSession(chatId, session);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
