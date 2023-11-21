package ru.skillbox.socialnet.zeronebot.handler.dialog;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.dto.session.DialogSession;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.WebSocketService;

import java.io.IOException;

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
    public void handle(SessionRq request) throws IOException {
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
