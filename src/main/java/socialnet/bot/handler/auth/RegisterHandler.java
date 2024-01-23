package socialnet.bot.handler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import socialnet.bot.dto.enums.state.RegisterState;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.session.RegisterSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.KeyboardService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.RegisterSessionService;

import static socialnet.bot.constant.Common.REGISTER;

@Component
@RequiredArgsConstructor
public class RegisterHandler extends UserRequestHandler {
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;
    private final RegisterSessionService registerSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isCallback(request.getUpdate(), REGISTER);
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();

        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardService.buildMenuWithCancel();
        telegramService.sendMessage(
                chatId,
                "Как мне вас называть❓",
                replyKeyboardMarkup);

        RegisterSession registerSession = request.getRegisterSession();
        registerSession.setRegisterState(RegisterState.NAME_WAIT);
        registerSessionService.saveSession(chatId, registerSession);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
