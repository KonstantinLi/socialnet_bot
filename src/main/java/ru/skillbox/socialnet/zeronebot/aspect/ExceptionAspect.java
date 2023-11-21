package ru.skillbox.socialnet.zeronebot.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.exception.*;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.TokenService;
import ru.skillbox.socialnet.zeronebot.service.session.*;

@Component
@Aspect
@Order(2)
@Slf4j
@RequiredArgsConstructor
public class ExceptionAspect {
    private final TokenService tokenService;
    private final TelegramService telegramService;
    private final KeyboardService keyboardService;

    private final EditSessionService editSessionService;
    private final PostSessionService postSessionService;
    private final LoginSessionService loginSessionService;
    private final DialogSessionService dialogSessionService;
    private final FilterSessionService filterSessionService;
    private final CommentSessionService commentSessionService;
    private final FriendsSessionService friendsSessionService;
    private final RegisterSessionService registerSessionService;


    @Around("execution(* ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler.handle(" +
            "ru.skillbox.socialnet.zeronebot.dto.request.SessionRq)) && " +
            "within(ru.skillbox.socialnet.zeronebot.handler.auth..*)")
    public Object aroundAuthExceptionHandleAdvice(ProceedingJoinPoint joinPoint) {
        SessionRq sessionRq = (SessionRq) joinPoint.getArgs()[0];
        Long chatId = sessionRq.getChatId();

        try {
            return joinPoint.proceed();

        } catch (BadRequestException ex) {
            InlineKeyboardMarkup markupInLine = keyboardService.buildAuthMenu();

            if (sessionRq.getRegisterSession().getRegisterState() != null) {
                telegramService.sendMessage(
                        chatId,
                        "Ошибка регистрации: " + ex.getErrorRs().getErrorDescription(),
                        markupInLine);
            } else {
                telegramService.sendMessage(
                        chatId,
                        "Ошибка авторизации: " + ex.getErrorRs().getErrorDescription(),
                        markupInLine);
            }

            loginSessionService.deleteSession(chatId);
            registerSessionService.deleteSession(chatId);

        } catch (Throwable ex) {
            telegramService.sendMessage(chatId, "Возникла ошибка. Пожалуйста, попробуйте позже");
            loginSessionService.deleteSession(chatId);
            registerSessionService.deleteSession(chatId);
            log.error(ex.getMessage());
        }

        return null;
    }

    @Around("execution(* ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler.handle(" +
            "ru.skillbox.socialnet.zeronebot.dto.request.SessionRq)) && " +
            "(target(ru.skillbox.socialnet.zeronebot.handler.auth.LogoutHandler) || " +
            "(!target(ru.skillbox.socialnet.zeronebot.handler.common.StartCommandHandler) && " +
            "!within(ru.skillbox.socialnet.zeronebot.handler.auth..*)))")
    public Object aroundExceptionHandleAdvice(ProceedingJoinPoint joinPoint) {
        SessionRq sessionRq = (SessionRq) joinPoint.getArgs()[0];
        Long chatId = sessionRq.getChatId();

        InlineKeyboardMarkup markupInLine = keyboardService.buildAuthMenu();

        try {
            tokenService.getToken(sessionRq.getUserSession().getId());
            return joinPoint.proceed();

        } catch (IdException | TokenException ex) {
            telegramService.sendMessage(
                    chatId,
                    "Вы не авторизованы",
                    markupInLine);

        } catch (BadRequestException ex) {
            if (sessionRq.getRegisterSession().getRegisterState() != null) {
                telegramService.sendMessage(
                        chatId,
                        ex.getErrorRs().getErrorDescription(),
                        markupInLine);
            } else {
                telegramService.sendMessage(chatId, ex.getErrorRs().getErrorDescription());
            }

        } catch (IllegalFilterException | OutOfListException ex) {
            telegramService.sendMessage(chatId, ex.getMessage());

        } catch (Throwable ex) {
            ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
            keyboardRemove.setRemoveKeyboard(true);
            telegramService.sendMessage(
                    chatId,
                    "Возникла ошибка. Пожалуйста, попробуйте позже",
                    keyboardRemove);

            editSessionService.deleteSession(chatId);
            postSessionService.deleteSession(chatId);
            loginSessionService.deleteSession(chatId);
            dialogSessionService.deleteSession(chatId);
            filterSessionService.deleteSession(chatId);
            commentSessionService.deleteSession(chatId);
            friendsSessionService.deleteSession(chatId);
            registerSessionService.deleteSession(chatId);
            log.error(ex.getMessage());
        }

        return null;
    }
}
