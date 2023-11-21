package ru.skillbox.socialnet.zeronebot.handler.dialog;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.constant.Dialog;
import ru.skillbox.socialnet.zeronebot.constant.Person;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.dto.response.DialogRs;
import ru.skillbox.socialnet.zeronebot.dto.session.DialogSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.DialogService;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.WebSocketService;
import ru.skillbox.socialnet.zeronebot.service.session.DialogSessionService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class DialogOpenHandler extends UserRequestHandler {
    private final DialogService dialogService;
    private final TelegramService telegramService;
    private final KeyboardService keyboardService;
    private final WebSocketService webSocketService;
    private final DialogSessionService dialogSessionService;

    @Override
    public boolean isApplicable(SessionRq request) {
        Update update = request.getUpdate();

        return isCallbackStartsWith(update, Dialog.MESSAGE.getCommand()) ||
                isCallbackStartsWith(update, Person.MESSAGE.getCommand());
    }

    @Override
    public void handle(SessionRq request) throws IOException {
        Long chatId = request.getChatId();
        Long userId = request.getUserSession().getId();

        DialogSession dialogSession = request.getDialogSession();
        DialogRs dialog = dialogService.getDialog(request);

        if (dialog == null) {
            return;
        }

        Long dialogId = dialog.getId();

        ReplyKeyboardMarkup replyKeyboardMarkup = keyboardService.buildOpenedDialogMenu();
        telegramService.sendMessage(
                chatId,
                "<b>Вы вошли в диалог</b>",
                replyKeyboardMarkup);

        dialogService.openDialog(request, dialog);

        StompSession stompSession = webSocketService.connect(userId, chatId, dialogId);
        dialogSession.setStompSession(stompSession);
        dialogSession.setId(dialogId);
        dialogSessionService.saveSession(chatId, dialogSession);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
