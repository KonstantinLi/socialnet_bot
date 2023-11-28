package ru.skillbox.socialnet.zeronebot.handler.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.skillbox.socialnet.zeronebot.constant.Account;
import ru.skillbox.socialnet.zeronebot.dto.enums.state.EditState;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.dto.session.EditSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.EditSessionService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ProfileEditSetHandler extends UserRequestHandler {
    private final TelegramService telegramService;
    private final EditSessionService editSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        Update update = request.getUpdate();

        return request.getEditSession().getEditRq() != null &&
                (isTextMessage(update, Account.ABOUT) ||
                        isTextMessage(update, Account.CITY) ||
                        isTextMessage(update, Account.COUNTRY) ||
                        isTextMessage(update, Account.PHONE) ||
                        isTextMessage(update, Account.BIRTH_DATE) ||
                        isTextMessage(update, Account.FIRST_NAME) ||
                        isTextMessage(update, Account.LAST_NAME));
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long chatId = request.getChatId();
        Update update = request.getUpdate();
        EditSession editSession = request.getEditSession();

        if (isTextMessage(update, Account.ABOUT)) {
            editSession.setEditState(EditState.ABOUT_WAIT);
        } else if (isTextMessage(update, Account.CITY)) {
            editSession.setEditState(EditState.CITY_WAIT);
        } else if (isTextMessage(update, Account.COUNTRY)) {
            editSession.setEditState(EditState.COUNTRY_WAIT);
        } else if (isTextMessage(update, Account.PHONE)) {
            editSession.setEditState(EditState.PHONE_WAIT);
        } else if (isTextMessage(update, Account.FIRST_NAME)) {
            editSession.setEditState(EditState.FIRST_NAME_WAIT);
        } else if (isTextMessage(update, Account.LAST_NAME)) {
            editSession.setEditState(EditState.LAST_NAME_WAIT);
        } else if (isTextMessage(update, Account.BIRTH_DATE)) {
            editSession.setEditState(EditState.BIRTHDATE_WAIT);
            telegramService.sendMessage(chatId, "<i>Формат 10.10.2010</i>");
        }

        editSessionService.saveSession(chatId, editSession);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
