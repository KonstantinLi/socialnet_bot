package ru.skillbox.socialnet.zeronebot.handler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import ru.skillbox.socialnet.zeronebot.dto.session.UserSession;
import ru.skillbox.socialnet.zeronebot.dto.enums.SessionState;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.helper.KeyboardHelper;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.UserSessionService;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Common.LOGOUT;

@Component
@RequiredArgsConstructor
public class LogoutHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final TelegramService telegramService;
    private final UserSessionService userSessionService;

    private final KeyboardHelper keyboardHelper;

    @Override
    public boolean isApplicable(UserRq request) {
        return isTextMessage(request.getUpdate(), LOGOUT);
    }

    @Override
    public void handle(UserRq request) throws IOException {
        httpService.logout(request.getUserSession().getId());

        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
        keyboardRemove.setRemoveKeyboard(true);
        telegramService.sendMessage(request.getChatId(),
                "Вы успешно вышли из системы",
                keyboardRemove);

        InlineKeyboardMarkup markupInLine = keyboardHelper.buildAuthMenu();
        telegramService.sendMessage(request.getChatId(),
                "Вам необходимо авторизоваться!",
                markupInLine);

        UserSession session = request.getUserSession();
        session.setSessionState(SessionState.UNAUTHORIZED);
        session.setId(null);
        userSessionService.saveSession(request.getChatId(), session);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}