package socialnet.bot.handler.dialog;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import socialnet.bot.constant.Dialog;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.session.DialogSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.DialogSessionService;

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
    public void handle(SessionRq request) throws Exception {
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
