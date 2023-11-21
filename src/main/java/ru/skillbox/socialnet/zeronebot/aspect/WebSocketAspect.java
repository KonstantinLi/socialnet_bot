package ru.skillbox.socialnet.zeronebot.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.dto.session.DialogSession;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.session.DialogSessionService;

@Component
@Aspect
@Order(1)
@RequiredArgsConstructor
public class WebSocketAspect {
    private final TelegramService telegramService;
    private final DialogSessionService dialogSessionService;

    @Before("execution(* ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler.handle(" +
            "ru.skillbox.socialnet.zeronebot.dto.request.UserRq)) && " +
            "!target(ru.skillbox.socialnet.zeronebot.handler.dialog.SendMessageHandler) && " +
            "!target(ru.skillbox.socialnet.zeronebot.handler.dialog.DialogCloseHandler) && " +
            "!within(ru.skillbox.socialnet.zeronebot.handler.auth..*)")
    public void aroundWebSocketDisconnectAdvice(JoinPoint joinPoint) {
        UserRq userRq = (UserRq) joinPoint.getArgs()[0];
        DialogSession dialogSession = userRq.getDialogSession();

        if (dialogSession != null) {
            StompSession stompSession = dialogSession.getStompSession();
            if (stompSession != null) {
                stompSession.disconnect();
                dialogSession.setStompSession(null);
                dialogSessionService.saveSession(userRq.getChatId(), dialogSession);

                telegramService.sendMessage(
                        userRq.getChatId(),
                        "<b>Вы вышли из диалога</b>");
            }
        }
    }
}
