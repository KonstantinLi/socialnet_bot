package socialnet.bot.handler.dialog;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.session.DialogSession;
import socialnet.bot.handler.UserRequestHandler;
import socialnet.bot.service.WebSocketService;

@Component
@RequiredArgsConstructor
public class SendMessageHandler extends UserRequestHandler {
    private final WebSocketService webSocketService;

    @Override
    public boolean isApplicable(SessionRq request) {
        Update update = request.getUpdate();

        return !isCommand(update) && isTextMessage(request.getUpdate()) &&
                request.getDialogSession().getStompSession() != null;
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long userId = request.getUserSession().getId();

        DialogSession dialogSession = request.getDialogSession();
        StompSession stompSession = dialogSession.getStompSession();

        Long dialogId = dialogSession.getId();
        String message = request.getUpdate().getMessage().getText();

        webSocketService.sendMessage(stompSession, userId, dialogId, message);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
