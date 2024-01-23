package socialnet.bot.handler.dialog;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import socialnet.bot.constant.Dialog;
import socialnet.bot.constant.Person;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.response.DialogRs;
import socialnet.bot.dto.session.DialogSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.DialogService;
import socialnet.bot.service.KeyboardService;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.WebSocketService;
import socialnet.bot.service.session.DialogSessionService;

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
    public void handle(SessionRq request) throws Exception {
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
