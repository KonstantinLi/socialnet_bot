package socialnet.bot.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import socialnet.bot.dto.request.SessionRq;
import socialnet.bot.dto.session.DialogSession;
import socialnet.bot.service.TelegramService;
import socialnet.bot.service.session.DialogSessionService;

@Component
@Aspect
@Order(1)
@RequiredArgsConstructor
public class WebSocketAspect {
    private final TelegramService telegramService;
    private final DialogSessionService dialogSessionService;

    @Before("execution(* socialnet.bot.handler.UserRequestHandler.handle(" +
            "socialnet.bot.dto.request.SessionRq)) && " +
            "!target(socialnet.bot.handler.dialog.SendMessageHandler) && " +
            "!target(socialnet.bot.handler.dialog.DialogCloseHandler) && " +
            "!within(socialnet.bot.handler.auth..*)")
    public void aroundWebSocketDisconnectAdvice(JoinPoint joinPoint) {
        SessionRq sessionRq = (SessionRq) joinPoint.getArgs()[0];

        Long chatId = sessionRq.getChatId();
        DialogSession dialogSession = sessionRq.getDialogSession();

        if (dialogSession != null) {
            StompSession stompSession = dialogSession.getStompSession();
            if (stompSession != null) {
                stompSession.disconnect();
                dialogSession.setStompSession(null);
                dialogSessionService.saveSession(chatId, dialogSession);

                telegramService.sendMessage(
                        chatId,
                        "<b>Вы вышли из диалога</b>");
            }
        }
    }
}
