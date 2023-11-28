package ru.skillbox.socialnet.zeronebot.handler.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.dto.session.EditSession;
import ru.skillbox.socialnet.zeronebot.dto.session.LoginSession;
import ru.skillbox.socialnet.zeronebot.dto.session.PostSession;
import ru.skillbox.socialnet.zeronebot.dto.session.RegisterSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.EditSessionService;
import ru.skillbox.socialnet.zeronebot.service.session.LoginSessionService;
import ru.skillbox.socialnet.zeronebot.service.session.PostSessionService;
import ru.skillbox.socialnet.zeronebot.service.session.RegisterSessionService;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Common.CANCEL;

@Component
@RequiredArgsConstructor
public class CancelHandler extends UserRequestHandler {
    private final KeyboardService keyboardService;
    private final TelegramService telegramService;
    private final EditSessionService editSessionService;
    private final PostSessionService postSessionService;
    private final LoginSessionService loginSessionService;
    private final RegisterSessionService registerSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isTextMessage(request.getUpdate(), CANCEL);
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();

        EditSession editSession = request.getEditSession();
        PostSession postSession = request.getPostSession();
        LoginSession loginSession = request.getLoginSession();
        RegisterSession registerSession = request.getRegisterSession();

        InlineKeyboardMarkup markupInLine = keyboardService.buildAuthMenu();

        if (loginSession.getLoginState() != null) {
            telegramService.sendMessage(
                    chatId,
                    "Вы не завершили авторизацию. Пожалуйста, попробуйте снова",
                    markupInLine);
            loginSessionService.deleteSession(chatId);

        } else if (registerSession.getRegisterState() != null) {
            telegramService.sendMessage(
                    chatId,
                    "Вы не завершили регистрацию. Пожалуйста, попробуйте снова",
                    markupInLine);
            registerSessionService.deleteSession(chatId);

        } else if (postSession.getPostState() != null) {
            ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
            keyboardRemove.setRemoveKeyboard(true);
            telegramService.sendMessage(
                    chatId,
                    "Вы прервали создание поста",
                    keyboardRemove);
            postSessionService.deleteSession(chatId);

        } else if (editSession.getEditRq() != null) {
            ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
            keyboardRemove.setRemoveKeyboard(true);
            telegramService.sendMessage(
                    chatId,
                    "Изменения не сохранены",
                    keyboardRemove);
            editSessionService.deleteSession(chatId);
        }
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
