package ru.skillbox.socialnet.zeronebot.handler.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.request.EditRq;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.dto.session.EditSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.EditSessionService;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Person.EDIT;

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
