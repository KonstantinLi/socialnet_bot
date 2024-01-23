package socialnet.bot.handler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import socialnet.bot.dto.enums.state.LoginState;
import socialnet.bot.dto.enums.state.RegisterState;
import socialnet.bot.dto.enums.state.SessionState;
import socialnet.bot.dto.request.LoginRq;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.response.PersonRs;
import socialnet.bot.dto.session.LoginSession;
import socialnet.bot.dto.session.RegisterSession;
import socialnet.bot.dto.session.UserSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.FormatService;
import socialnet.bot.service.HttpService;
import socialnet.bot.service.KeyboardService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.LoginSessionService;
import socialnet.bot.service.session.RegisterSessionService;
import socialnet.bot.service.session.UserSessionService;

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
