package ru.skillbox.socialnet.zeronebot.handler.dialog;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import ru.skillbox.socialnet.zeronebot.constant.Dialog;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.dto.session.DialogSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.DialogSessionService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class DialogCloseHandler extends UserRequestHandler {
    private final TelegramService telegramService;
    private final DialogSessionService dialogSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isTextMessage(request.getUpdate(), Dialog.CLOSE.getText());
    }

    @Override
    public void handle(SessionRq request) throws IOException {
        Long chatId = request.getChatId();
        DialogSession dialogSession = request.getDialogSession();

        if (dialogSession.getStompSession() != null) {
            ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
            keyboardRemove.setRemoveKeyboard(true);
            telegramService.sendMessage(
                    chatId,
                    "<b>Вы вышли из диалога</b>",
                    keyboardRemove);

            StompSession stompSession = dialogSession.getStompSession();
            if (stompSession != null) {
                stompSession.disconnect();
                dialogSession.setStompSession(null);
                dialogSessionService.saveSession(chatId, dialogSession);
            }
        }
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
