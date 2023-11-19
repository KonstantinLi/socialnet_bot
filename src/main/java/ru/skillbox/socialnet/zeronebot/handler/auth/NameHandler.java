package ru.skillbox.socialnet.zeronebot.handler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.RegisterState;
import ru.skillbox.socialnet.zeronebot.dto.request.RegisterRq;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.session.RegisterSession;
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
        Long chatId = request.getChatId();
        String name = request.getUpdate().getMessage().getText();

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
