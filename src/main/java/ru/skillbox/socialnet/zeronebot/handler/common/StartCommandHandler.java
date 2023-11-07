package ru.skillbox.socialnet.zeronebot.handler.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.config.ZeroneProperties;
import ru.skillbox.socialnet.zeronebot.dto.session.UserSession;
import ru.skillbox.socialnet.zeronebot.dto.enums.SessionState;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.helper.KeyboardHelper;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.UserSessionService;

import java.io.IOException;
import java.net.URL;

@Component
@RequiredArgsConstructor
public class StartCommandHandler extends UserRequestHandler {
    private final TelegramService telegramService;
    private final UserSessionService userSessionService;

    private final KeyboardHelper keyboardHelper;
    private final ZeroneProperties zeroneProperties;

    @Override
    public boolean isApplicable(UserRq request) {
        return isCommand(request.getUpdate(), "/start");
    }

    @Override
    public void handle(UserRq request) throws IOException {
        InlineKeyboardMarkup markupInLine = keyboardHelper.buildAuthMenu();
        telegramService.sendPhotoURL(
                request.getChatId(),
                new URL(zeroneProperties.getPhoto()),
                zeroneProperties.getWelcome(),
                markupInLine);

        UserSession session = request.getUserSession();
        session.setSessionState(SessionState.UNAUTHORIZED);
        userSessionService.saveSession(request.getChatId(), session);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
