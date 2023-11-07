package ru.skillbox.socialnet.zeronebot.handler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.session.LoginSession;
import ru.skillbox.socialnet.zeronebot.dto.session.RegisterSession;
import ru.skillbox.socialnet.zeronebot.dto.enums.LoginState;
import ru.skillbox.socialnet.zeronebot.dto.enums.RegisterState;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.helper.KeyboardHelper;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.LoginSessionService;
import ru.skillbox.socialnet.zeronebot.service.session.RegisterSessionService;

@Component
@RequiredArgsConstructor
public class EmailHandler extends UserRequestHandler {
    private final TelegramService telegramService;
    private final LoginSessionService loginSessionService;
    private final RegisterSessionService registerSessionService;

    private final KeyboardHelper keyboardHelper;

    @Override
    public boolean isApplicable(UserRq request) {
        LoginState loginState = request.getLoginSession().getLoginState();
        RegisterState registerState = request.getRegisterSession().getRegisterState();

        return isTextMessage(request.getUpdate()) &&
                (loginState == LoginState.EMAIL_WAIT ||
                    registerState == RegisterState.EMAIL_WAIT);
    }

    @Override
    public void handle(UserRq request) {
        LoginSession loginSession = request.getLoginSession();
        RegisterSession registerSession = request.getRegisterSession();

        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardHelper.buildMenuWithCancel();
        telegramService.sendMessage(request.getChatId(),
                "Теперь введите пароль:",
                replyKeyboardMarkup);

        String email = request.getUpdate().getMessage().getText();

        if (loginSession.getLoginState() == LoginState.EMAIL_WAIT) {

            loginSession.setEmail(email);
            loginSession.setLoginState(LoginState.PASSWORD_WAIT);
            loginSessionService.saveSession(request.getChatId(), loginSession);

        } else if (registerSession.getRegisterState() == RegisterState.EMAIL_WAIT) {

            registerSession.setEmail(email);
            registerSession.setRegisterState(RegisterState.PASSWORD_WAIT);
            registerSessionService.saveSession(request.getChatId(), registerSession);
        }
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
