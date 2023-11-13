package ru.skillbox.socialnet.zeronebot.handler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.session.LoginSession;
import ru.skillbox.socialnet.zeronebot.dto.session.RegisterSession;
import ru.skillbox.socialnet.zeronebot.dto.session.UserSession;
import ru.skillbox.socialnet.zeronebot.dto.enums.LoginState;
import ru.skillbox.socialnet.zeronebot.dto.enums.RegisterState;
import ru.skillbox.socialnet.zeronebot.dto.enums.SessionState;
import ru.skillbox.socialnet.zeronebot.dto.request.LoginRq;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.response.PersonRs;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.LoginSessionService;
import ru.skillbox.socialnet.zeronebot.service.session.RegisterSessionService;
import ru.skillbox.socialnet.zeronebot.service.session.UserSessionService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class PasswordHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final TelegramService telegramService;
    private final UserSessionService userSessionService;
    private final LoginSessionService loginSessionService;
    private final RegisterSessionService registerSessionService;

    private final KeyboardService keyboardService;

    @Override
    public boolean isApplicable(UserRq request) {
        LoginState loginState = request.getLoginSession().getLoginState();
        RegisterState registerState = request.getRegisterSession().getRegisterState();

        return isTextMessage(request.getUpdate()) &&
                (loginState == LoginState.PASSWORD_WAIT ||
                        registerState == RegisterState.PASSWORD_WAIT);
    }

    @Override
    public void handle(UserRq request) throws IOException {
        UserSession userSession = request.getUserSession();
        LoginSession loginSession = request.getLoginSession();
        RegisterSession registerSession = request.getRegisterSession();

        String password = request.getUpdate().getMessage().getText();

        if (loginSession.getLoginState() == LoginState.PASSWORD_WAIT) {
            String email = loginSession.getEmail();

            LoginRq loginRq = LoginRq.builder()
                    .login(email)
                    .password(password)
                    .build();

            PersonRs personRs = httpService.login(loginRq);

            ReplyKeyboardMarkup replyKeyboardMarkup = keyboardService.buildMainMenu();
            telegramService.sendMessage(request.getChatId(),
                    "Приветствуем, "
                            + personRs.getFirstName() + " " + personRs.getLastName()
                            + "!",
                    replyKeyboardMarkup);

            loginSessionService.deleteSession(request.getChatId());

            userSession.setSessionState(SessionState.AUTHORIZED);
            userSession.setId(personRs.getId());
            userSessionService.saveSession(request.getChatId(), userSession);

        } else if (registerSession.getRegisterState() == RegisterState.PASSWORD_WAIT) {
            ReplyKeyboardMarkup replyKeyboardMarkup = keyboardService.buildMenuWithCancel();
            telegramService.sendMessage(request.getChatId(),
                    "Подтвердите пароль:",
                    replyKeyboardMarkup);

            registerSession.setPassword(password);
            registerSession.setRegisterState(RegisterState.PASSWORD_CONFIRM);
            registerSessionService.saveSession(request.getChatId(), registerSession);
        }
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
