package ru.skillbox.socialnet.zeronebot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.session.LoginSession;
import ru.skillbox.socialnet.zeronebot.dto.session.UserSession;
import ru.skillbox.socialnet.zeronebot.dto.enums.LoginState;
import ru.skillbox.socialnet.zeronebot.dto.enums.SessionState;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.helper.KeyboardHelper;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.LoginSessionService;
import ru.skillbox.socialnet.zeronebot.service.session.UserSessionService;

@Component
@RequiredArgsConstructor
public class ExceptionHandler {
    private final TelegramService telegramService;
    private final UserSessionService userSessionService;
    private final LoginSessionService loginSessionService;

    private final KeyboardHelper keyboardHelper;

    public void handle(UserRq request) {
        UserSession userSession = request.getUserSession();
        LoginSession loginSession = request.getLoginSession();

        InlineKeyboardMarkup markupInLine = keyboardHelper.buildAuthMenu();

        if (loginSession.getLoginState() == LoginState.EMAIL_WAIT ||
                loginSession.getLoginState() == LoginState.PASSWORD_WAIT) {

            telegramService.sendMessage(request.getChatId(),
                    "Введены не правильный логин или пароль. Попробуйте еще раз",
                    markupInLine);

            loginSession.setEmail(null);
            loginSession.setLoginState(null);
            loginSessionService.saveSession(request.getChatId(), loginSession);

        } else {
            telegramService.sendMessage(request.getChatId(),
                    "Вы не авторизованы",
                    markupInLine);
        }

        userSession.setSessionState(SessionState.UNAUTHORIZED);
        userSession.setId(null);
        userSessionService.saveSession(request.getChatId(), userSession);
    }
}
