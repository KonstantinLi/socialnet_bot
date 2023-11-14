package ru.skillbox.socialnet.zeronebot.handler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.enums.RegisterState;
import ru.skillbox.socialnet.zeronebot.dto.request.RegisterRq;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.session.RegisterSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.RegisterSessionService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CaptchaHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final TelegramService telegramService;
    private final RegisterSessionService registerSessionService;

    private final KeyboardService keyboardService;

    @Override
    public boolean isApplicable(UserRq request) {
        RegisterState registerState = request.getRegisterSession().getRegisterState();

        return isTextMessage(request.getUpdate()) &&
                registerState == RegisterState.CAPTCHA_WAIT;
    }

    @Override
    public void handle(UserRq request) throws IOException {
        RegisterSession registerSession = request.getRegisterSession();

        String captcha = request.getUpdate().getMessage().getText();
        String name = registerSession.getName();

        RegisterRq registerRq = RegisterRq.builder()
                .email(registerSession.getEmail())
                .passwd1(registerSession.getPassword())
                .passwd2(registerSession.getPasswordConfirm())
                .codeSecret(registerSession.getCaptchaCode())
                .code(captcha)
                .build();

        String[] nameParts = name.split("\\b");
        if (nameParts.length == 1) {
            registerRq.setFirstName(name);
        } else {
            registerRq.setFirstName(nameParts[0]);
            registerRq.setLastName(name.substring(nameParts[0].length() + 1).trim());
        }

        httpService.register(registerRq);

        InlineKeyboardMarkup markupInLine = keyboardService.buildAuthMenu();
        telegramService.sendMessage(
                request.getChatId(),
                "Вы успешно зарегистрировались!",
                markupInLine);

        registerSessionService.deleteSession(request.getChatId());
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
