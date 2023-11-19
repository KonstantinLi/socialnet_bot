package ru.skillbox.socialnet.zeronebot.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.exception.*;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;
import ru.skillbox.socialnet.zeronebot.service.TokenService;
import ru.skillbox.socialnet.zeronebot.service.session.*;

@Component
@Aspect
@RequiredArgsConstructor
public class ExceptionAspect {
    private final TokenService tokenService;
    private final TelegramService telegramService;
    private final KeyboardService keyboardService;

    private final PostSessionService postSessionService;
    private final LoginSessionService loginSessionService;
    private final FilterSessionService filterSessionService;
    private final CommentSessionService commentSessionService;
    private final FriendsSessionService friendsSessionService;
    private final RegisterSessionService registerSessionService;


    @Around("execution(* ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler.handle(" +
            "ru.skillbox.socialnet.zeronebot.dto.request.UserRq)) && " +
            "within(ru.skillbox.socialnet.zeronebot.handler.auth..*)")
    public Object aroundAuthExceptionHandleAdvice(ProceedingJoinPoint joinPoint) {
        UserRq userRq = (UserRq) joinPoint.getArgs()[0];

        try {
            return joinPoint.proceed();

        } catch (BadRequestException ex) {
            InlineKeyboardMarkup markupInLine = keyboardService.buildAuthMenu();

            if (userRq.getRegisterSession().getRegisterState() != null) {
                telegramService.sendMessage(userRq.getChatId(),
                        "Ошибка регистрации: " + ex.getErrorRs().getErrorDescription(),
                        markupInLine);
            } else {
                telegramService.sendMessage(userRq.getChatId(),
                        "Ошибка авторизации: " + ex.getErrorRs().getErrorDescription(),
                        markupInLine);
            }

            loginSessionService.deleteSession(userRq.getChatId());
            registerSessionService.deleteSession(userRq.getChatId());

        } catch (Throwable ex) {
            telegramService.sendMessage(userRq.getChatId(),
                    "Возникла ошибка. Пожалуйста, попробуйте позже");

            loginSessionService.deleteSession(userRq.getChatId());
            registerSessionService.deleteSession(userRq.getChatId());
        }

        return null;
    }

    @Around("execution(* ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler.handle(..)) && " +
            "(target(ru.skillbox.socialnet.zeronebot.handler.auth.LogoutHandler) || " +
            "(!target(ru.skillbox.socialnet.zeronebot.handler.common.StartCommandHandler) && " +
            "!within(ru.skillbox.socialnet.zeronebot.handler.auth..*)))")
    public Object aroundExceptionHandleAdvice(ProceedingJoinPoint joinPoint) {
        UserRq userRq = (UserRq) joinPoint.getArgs()[0];
        InlineKeyboardMarkup markupInLine = keyboardService.buildAuthMenu();

        try {
            tokenService.getToken(userRq.getUserSession().getId());
            return joinPoint.proceed();

        } catch (IdException | TokenException ex) {
            telegramService.sendMessage(
                    userRq.getChatId(),
                    "Вы не авторизованы",
                    markupInLine);

        } catch (BadRequestException ex) {
            if (userRq.getRegisterSession().getRegisterState() != null) {
                telegramService.sendMessage(userRq.getChatId(),
                        ex.getErrorRs().getErrorDescription(),
                        markupInLine);
            } else {
                telegramService.sendMessage(userRq.getChatId(),
                        ex.getErrorRs().getErrorDescription());
            }

        } catch (IllegalFilterException | OutOfListException ex) {
            telegramService.sendMessage(userRq.getChatId(), ex.getMessage());

        } catch (Throwable ex) {
            telegramService.sendMessage(userRq.getChatId(),
                    "Возникла ошибка. Пожалуйста, попробуйте позже");

            Long chatId = userRq.getChatId();
            postSessionService.deleteSession(chatId);
            loginSessionService.deleteSession(chatId);
            filterSessionService.deleteSession(chatId);
            commentSessionService.deleteSession(chatId);
            friendsSessionService.deleteSession(chatId);
            registerSessionService.deleteSession(chatId);
        }

        return null;
    }
}
