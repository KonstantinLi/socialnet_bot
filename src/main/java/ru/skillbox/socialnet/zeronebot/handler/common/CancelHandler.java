package ru.skillbox.socialnet.zeronebot.handler.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.session.LoginSession;
import ru.skillbox.socialnet.zeronebot.dto.session.RegisterSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.LoginSessionService;
import ru.skillbox.socialnet.zeronebot.service.session.RegisterSessionService;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Common.CANCEL;

@Component
@RequiredArgsConstructor
public class CancelHandler extends UserRequestHandler {
    private final TelegramService telegramService;
    private final LoginSessionService loginSessionService;
    private final RegisterSessionService registerSessionService;

    private final KeyboardService keyboardService;

    @Override
    public boolean isApplicable(UserRq request) {
        return isTextMessage(request.getUpdate(), CANCEL);
    }

    @Override
    public void handle(UserRq request) throws IOException {
        LoginSession loginSession = request.getLoginSession();
        RegisterSession registerSession = request.getRegisterSession();

        InlineKeyboardMarkup markupInLine = keyboardService.buildAuthMenu();

        if (loginSession.getLoginState() != null) {
            telegramService.sendMessage(
                    request.getChatId(),
                    "Вы не завершили авторизацию. Пожалуйста, попробуйте снова",
                    markupInLine);

            loginSessionService.deleteSession(request.getChatId());
        } else if (registerSession.getRegisterState() != null) {
            telegramService.sendMessage(
                    request.getChatId(),
                    "Вы не завершили регистрацию. Пожалуйста, попробуйте снова",
                    markupInLine);

            registerSessionService.deleteSession(request.getChatId());
        }
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
