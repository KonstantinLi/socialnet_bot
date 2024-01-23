package socialnet.bot.handler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import socialnet.bot.dto.enums.state.LoginState;
import socialnet.bot.dto.enums.state.RegisterState;
import socialnet.bot.dto.request.LoginRq;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.session.LoginSession;
import socialnet.bot.dto.session.RegisterSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.KeyboardService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.LoginSessionService;
import socialnet.bot.service.session.RegisterSessionService;

@Component
@RequiredArgsConstructor
public class EmailHandler extends UserRequestHandler {
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;
    private final LoginSessionService loginSessionService;
    private final RegisterSessionService registerSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        LoginState loginState = request.getLoginSession().getLoginState();
        RegisterState registerState = request.getRegisterSession().getRegisterState();

        return isTextMessage(request.getUpdate()) &&
                (loginState == LoginState.EMAIL_WAIT ||
                    registerState == RegisterState.EMAIL_WAIT);
    }

    @Override
    public void handle(SessionRq request) throws Exception{
        Long chatId = request.getChatId();

        LoginSession loginSession = request.getLoginSession();
        RegisterSession registerSession = request.getRegisterSession();

        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardService.buildMenuWithCancel();
        telegramService.sendMessage(chatId,
                "Теперь введите пароль:",
                replyKeyboardMarkup);

        String email = request.getUpdate().getMessage().getText().trim();

        if (loginSession.getLoginState() == LoginState.EMAIL_WAIT) {
            LoginRq loginRq = LoginRq.builder().login(email).build();
            loginSession.setLoginRq(loginRq);
            loginSession.setLoginState(LoginState.PASSWORD_WAIT);
            loginSessionService.saveSession(chatId, loginSession);

        } else if (registerSession.getRegisterState() == RegisterState.EMAIL_WAIT) {
            registerSession.getRegisterRq().setEmail(email);
            registerSession.setRegisterState(RegisterState.PASSWORD_WAIT);
            registerSessionService.saveSession(chatId, registerSession);
        }
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
