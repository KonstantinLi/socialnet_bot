package socialnet.bot.handler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import socialnet.bot.dto.enums.state.LoginState;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.session.LoginSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.KeyboardService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.LoginSessionService;

import static socialnet.bot.constant.Common.LOGIN;

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
