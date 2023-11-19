package ru.skillbox.socialnet.zeronebot.handler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.RegisterState;
import ru.skillbox.socialnet.zeronebot.dto.request.RegisterRq;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.response.CaptchaRs;
import ru.skillbox.socialnet.zeronebot.dto.session.RegisterSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.RegisterSessionService;

import java.io.IOException;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class PasswordConfirmHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;
    private final RegisterSessionService registerSessionService;

    @Override
    public boolean isApplicable(UserRq request) {
        RegisterState registerState = request.getRegisterSession().getRegisterState();

        return isTextMessage(request.getUpdate()) &&
                registerState == RegisterState.PASSWORD_CONFIRM;
    }

    @Override
    public void handle(UserRq request) throws IOException {
        Long chatId = request.getChatId();
        String passwordConfirm = request.getUpdate().getMessage().getText();

        CaptchaRs captchaRs = httpService.captcha();
        byte[] captchaBytes = Base64.getDecoder()
                .decode(captchaRs.getImage()
                        .replace("data:image/png;base64,", ""));

        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardService.buildMenuWithCancel();
        telegramService.sendPhotoBytes(
                chatId,
                captchaBytes,
                "captcha.png",
                "Введите код на картинке:",
                replyKeyboardMarkup);

        RegisterSession registerSession = request.getRegisterSession();

        RegisterRq registerRq = registerSession.getRegisterRq();
        registerRq.setPasswd2(passwordConfirm);
        registerRq.setCodeSecret(captchaRs.getCode());

        registerSession.setRegisterState(RegisterState.CAPTCHA_WAIT);
        registerSessionService.saveSession(chatId, registerSession);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
