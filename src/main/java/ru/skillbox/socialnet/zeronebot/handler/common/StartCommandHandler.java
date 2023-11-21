package ru.skillbox.socialnet.zeronebot.handler.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.config.ZeroneProperties;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.SessionState;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.dto.session.UserSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.UserSessionService;

import java.io.IOException;
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
    public void handle(SessionRq request) throws IOException {
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
