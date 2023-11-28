package ru.skillbox.socialnet.zeronebot.handler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.LoginState;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.dto.session.LoginSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.LoginSessionService;

import static ru.skillbox.socialnet.zeronebot.constant.Common.LOGIN;

@Component
@RequiredArgsConstructor
public class LoginHandler extends UserRequestHandler {
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;
    private final LoginSessionService loginSessionSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isCallback(request.getUpdate(), LOGIN);
    }

    @Override
    public void handle(SessionRq request) throws Exception{
        Long chatId = request.getChatId();
        LoginSession loginSession = request.getLoginSession();

        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardService.buildMenuWithCancel();
        telegramService.sendMessage(
                chatId,
                "Введите свою почту:",
                replyKeyboardMarkup);

        loginSession.setLoginState(LoginState.EMAIL_WAIT);
        loginSessionSessionService.saveSession(chatId, loginSession);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
