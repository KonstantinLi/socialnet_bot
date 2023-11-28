package ru.skillbox.socialnet.zeronebot.handler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import ru.skillbox.socialnet.zeronebot.constant.Menu;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.SessionState;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.dto.session.UserSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.*;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LogoutHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;

    private final PostSessionService postSessionService;
    private final UserSessionService userSessionService;
    private final EditSessionService editSessionService;
    private final LoginSessionService loginSessionService;
    private final DialogSessionService dialogSessionService;
    private final FilterSessionService filterSessionService;
    private final CommentSessionService commentSessionService;
    private final FriendsSessionService friendsSessionService;
    private final RegisterSessionService registerSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isCommand(request.getUpdate(), Menu.EXIT.getCommand());
    }

    @Override
    public void handle(SessionRq request) throws Exception {
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

        editSessionService.deleteSession(chatId);
        postSessionService.deleteSession(chatId);
        loginSessionService.deleteSession(chatId);
        dialogSessionService.deleteSession(chatId);
        filterSessionService.deleteSession(chatId);
        commentSessionService.deleteSession(chatId);
        friendsSessionService.deleteSession(chatId);
        registerSessionService.deleteSession(chatId);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
