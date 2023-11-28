package ru.skillbox.socialnet.zeronebot.handler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.LoginState;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.RegisterState;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.SessionState;
import ru.skillbox.socialnet.zeronebot.dto.request.LoginRq;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.dto.session.LoginSession;
import ru.skillbox.socialnet.zeronebot.dto.session.RegisterSession;
import ru.skillbox.socialnet.zeronebot.dto.session.UserSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.FormatService;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.LoginSessionService;
import ru.skillbox.socialnet.zeronebot.service.session.RegisterSessionService;
import ru.skillbox.socialnet.zeronebot.service.session.UserSessionService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class PasswordHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final FormatService formatService;
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;
    private final UserSessionService userSessionService;
    private final LoginSessionService loginSessionService;
    private final RegisterSessionService registerSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        LoginState loginState = request.getLoginSession().getLoginState();
        RegisterState registerState = request.getRegisterSession().getRegisterState();

        return isTextMessage(request.getUpdate()) &&
                (loginState == LoginState.PASSWORD_WAIT ||
                        registerState == RegisterState.PASSWORD_WAIT);
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();
        String password = request.getUpdate().getMessage().getText().trim();

        UserSession userSession = request.getUserSession();
        LoginSession loginSession = request.getLoginSession();
        RegisterSession registerSession = request.getRegisterSession();

        if (loginSession.getLoginState() == LoginState.PASSWORD_WAIT) {
            LoginRq loginRq = loginSession.getLoginRq();
            loginRq.setPassword(password);

            PersonRs personRs = httpService.login(loginRq);

            ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
            keyboardRemove.setRemoveKeyboard(true);
            telegramService.sendMessage(
                    chatId,
                    "Приветствуем, <b>" + formatService.getPersonName(personRs) + "</b>!",
                    keyboardRemove);

            loginSessionService.deleteSession(chatId);

            userSession.setSessionState(SessionState.AUTHORIZED);
            userSession.setId(personRs.getId());
            userSessionService.saveSession(chatId, userSession);

        } else if (registerSession.getRegisterState() == RegisterState.PASSWORD_WAIT) {
            ReplyKeyboardMarkup replyKeyboardMarkup = keyboardService.buildMenuWithCancel();
            telegramService.sendMessage(
                    chatId,
                    "Подтвердите пароль:",
                    replyKeyboardMarkup);

            registerSession.getRegisterRq().setPasswd1(password);
            registerSession.setRegisterState(RegisterState.PASSWORD_CONFIRM);
            registerSessionService.saveSession(chatId, registerSession);
        }
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
