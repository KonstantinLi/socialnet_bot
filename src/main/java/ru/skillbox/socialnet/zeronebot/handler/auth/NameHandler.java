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

@Component
@RequiredArgsConstructor
public class NameHandler extends UserRequestHandler {
    private final TelegramService telegramService;
    private final RegisterSessionService registerSessionService;

    private final KeyboardService keyboardService;

    @Override
    public boolean isApplicable(UserRq request) {
        RegisterState registerState = request.getRegisterSession().getRegisterState();

        return isTextMessage(request.getUpdate()) &&
                registerState == RegisterState.NAME_WAIT;
    }

    @Override
    public void handle(UserRq request) throws IOException {
        String name = request.getUpdate().getMessage().getText();

        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardService.buildMenuWithCancel();
        telegramService.sendMessage(request.getChatId(),
                "Введите свою почту:",
                replyKeyboardMarkup);

        RegisterSession registerSession = request.getRegisterSession();
        registerSession.setName(name);
        registerSession.setRegisterState(RegisterState.EMAIL_WAIT);
        registerSessionService.saveSession(request.getChatId(), registerSession);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
