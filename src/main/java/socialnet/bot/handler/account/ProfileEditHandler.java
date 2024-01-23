package socialnet.bot.handler.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import socialnet.bot.dto.request.EditRq;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.session.EditSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.KeyboardService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.EditSessionService;

import static socialnet.bot.constant.Person.EDIT;

@Component
@RequiredArgsConstructor
public class ProfileEditHandler extends UserRequestHandler {
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;
    private final EditSessionService editSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isCallback(request.getUpdate(), EDIT.getCommand());
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();

        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardService.buildEditMenu();
        telegramService.sendMessage(
                chatId,
                "<b>Редактирование профиля</b>",
                replyKeyboardMarkup);

        EditSession editSession = request.getEditSession();
        editSession.setEditRq(new EditRq());
        editSessionService.saveSession(chatId, editSession);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
