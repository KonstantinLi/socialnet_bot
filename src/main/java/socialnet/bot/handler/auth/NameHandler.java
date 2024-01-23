package socialnet.bot.handler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import socialnet.bot.dto.enums.state.RegisterState;
import socialnet.bot.dto.request.RegisterRq;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.session.RegisterSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.KeyboardService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.RegisterSessionService;

@Component
@RequiredArgsConstructor
public class NameHandler extends UserRequestHandler {
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;
    private final RegisterSessionService registerSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        RegisterState registerState = request.getRegisterSession().getRegisterState();
        return isTextMessage(request.getUpdate()) &&
                registerState == RegisterState.NAME_WAIT;
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();
        String name = request.getUpdate().getMessage().getText().trim();

        RegisterSession registerSession = request.getRegisterSession();

        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardService.buildMenuWithCancel();
        telegramService.sendMessage(
                chatId,
                "Введите свою почту:",
                replyKeyboardMarkup);

        RegisterRq registerRq = RegisterRq.builder().firstName(name).build();
        registerSession.setRegisterRq(registerRq);
        registerSession.setRegisterState(RegisterState.EMAIL_WAIT);
        registerSessionService.saveSession(chatId, registerSession);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
