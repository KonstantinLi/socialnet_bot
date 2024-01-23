package socialnet.bot.handler.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import socialnet.bot.constant.Menu;
import socialnet.bot.dto.enums.state.SessionState;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.session.UserSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.HttpService;
import socialnet.bot.service.KeyboardService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.*;

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
