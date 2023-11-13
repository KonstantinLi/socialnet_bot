package ru.skillbox.socialnet.zeronebot.handler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.enums.LoginState;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.session.LoginSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.LoginSessionService;

import static ru.skillbox.socialnet.zeronebot.constant.Common.LOGIN;

@Component
@RequiredArgsConstructor
public class LoginHandler extends UserRequestHandler {
    private final TelegramService telegramService;
    private final LoginSessionService loginSessionSessionService;

    private final KeyboardService keyboardService;

    @Override
    public boolean isApplicable(UserRq request) {
        return isCallback(request.getUpdate(), LOGIN);
    }

    @Override
    public void handle(UserRq request) {
        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardService.buildMenuWithCancel();
        telegramService.sendMessage(request.getChatId(),
                "Введите свою почту:",
                replyKeyboardMarkup);

        LoginSession loginSession = request.getLoginSession();
        loginSession.setLoginState(LoginState.EMAIL_WAIT);
        loginSessionSessionService.saveSession(request.getChatId(), loginSession);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
