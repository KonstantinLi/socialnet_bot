package ru.skillbox.socialnet.zeronebot.handler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.session.RegisterSession;
import ru.skillbox.socialnet.zeronebot.dto.enums.RegisterState;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.RegisterSessionService;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Common.REGISTER;

@Component
@RequiredArgsConstructor
public class RegisterHandler extends UserRequestHandler {
    private final TelegramService telegramService;
    private final RegisterSessionService registerSessionService;

    private final KeyboardService keyboardService;

    @Override
    public boolean isApplicable(UserRq request) {
        return isCallback(request.getUpdate(), REGISTER);
    }

    @Override
    public void handle(UserRq request) throws IOException {
        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardService.buildMenuWithCancel();
        telegramService.sendMessage(request.getChatId(),
                "Как мне вас называть?",
                replyKeyboardMarkup);

        RegisterSession registerSession = request.getRegisterSession();
        registerSession.setRegisterState(RegisterState.NAME_WAIT);
        registerSessionService.saveSession(request.getChatId(), registerSession);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
