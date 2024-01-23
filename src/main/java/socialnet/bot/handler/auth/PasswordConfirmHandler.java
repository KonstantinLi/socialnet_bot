package socialnet.bot.handler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import socialnet.bot.dto.enums.state.RegisterState;
import socialnet.bot.dto.request.RegisterRq;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.response.CaptchaRs;
import socialnet.bot.dto.session.RegisterSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.HttpService;
import socialnet.bot.service.KeyboardService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.RegisterSessionService;

import java.util.Base64;

@Component
@RequiredArgsConstructor
public class PasswordConfirmHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;
    private final RegisterSessionService registerSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        RegisterState registerState = request.getRegisterSession().getRegisterState();

        return isTextMessage(request.getUpdate()) &&
                registerState == RegisterState.PASSWORD_CONFIRM;
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();
        String passwordConfirm = request.getUpdate().getMessage().getText().trim();

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
