package ru.skillbox.socialnet.zeronebot.handler.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import ru.skillbox.socialnet.zeronebot.constant.Account;
import ru.skillbox.socialnet.zeronebot.dto.request.EditRq;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.EditSessionService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ProfileEditApplyHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final TelegramService telegramService;
    private final EditSessionService editSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isTextMessage(request.getUpdate(), Account.SAVE_CHANGES) &&
                request.getEditSession().getEditRq() != null;
    }

    @Override
    public void handle(SessionRq request) throws IOException {
        Long chatId = request.getChatId();
        EditRq editRq = request.getEditSession().getEditRq();

        httpService.editProfile(request, editRq);

        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
        keyboardRemove.setRemoveKeyboard(true);
        telegramService.sendMessage(
                chatId,
                "Информация обновлена",
                keyboardRemove);

        editSessionService.deleteSession(chatId);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
