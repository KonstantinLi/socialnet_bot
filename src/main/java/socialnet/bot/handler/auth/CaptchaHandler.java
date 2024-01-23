package socialnet.bot.handler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import socialnet.bot.dto.enums.state.RegisterState;
import socialnet.bot.dto.request.RegisterRq;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.session.RegisterSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.HttpService;
import socialnet.bot.service.KeyboardService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.RegisterSessionService;

@Component
@RequiredArgsConstructor
public class CaptchaHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;
    private final RegisterSessionService registerSessionService;


    @Override
    public boolean isApplicable(SessionRq request) {
        RegisterState registerState = request.getRegisterSession().getRegisterState();

        return isTextMessage(request.getUpdate()) &&
                registerState == RegisterState.CAPTCHA_WAIT;
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();
        String captcha = request.getUpdate().getMessage().getText();

        RegisterSession registerSession = request.getRegisterSession();
        RegisterRq registerRq = registerSession.getRegisterRq();
        registerRq.setCode(captcha);

        String name = registerRq.getFirstName();

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
                chatId,
                "<b>Вы успешно зарегистрировались!</b>",
                markupInLine);

        registerSessionService.deleteSession(chatId);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
