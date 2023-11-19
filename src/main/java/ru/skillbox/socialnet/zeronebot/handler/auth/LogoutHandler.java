package ru.skillbox.socialnet.zeronebot.handler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import ru.skillbox.socialnet.zeronebot.constant.Menu;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.SessionState;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.session.UserSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.UserSessionService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LogoutHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final TelegramService telegramService;
    private final UserSessionService userSessionService;

    private final KeyboardService keyboardService;

    @Override
    public boolean isApplicable(UserRq request) {
        return isCommand(request.getUpdate(), Menu.EXIT.getCommand());
    }

    @Override
    public void handle(UserRq request) throws IOException {
        Long chatId = request.getChatId();
        UserSession session = request.getUserSession();

        httpService.logout(request.getUserSession().getId());

        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
        keyboardRemove.setRemoveKeyboard(true);
        telegramService.sendMessage(
                chatId,
                "Вы успешно <b>вышли</b> из системы",
                keyboardRemove);

        InlineKeyboardMarkup markupInLine = keyboardService.buildAuthMenu();
        telegramService.sendMessage(
                chatId,
                "Вам необходимо авторизоваться!",
                markupInLine);

        session.setSessionState(SessionState.UNAUTHORIZED);
        session.setId(null);
        userSessionService.saveSession(chatId, session);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
