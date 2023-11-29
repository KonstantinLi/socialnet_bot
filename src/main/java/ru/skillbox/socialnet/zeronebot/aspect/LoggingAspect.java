package ru.skillbox.socialnet.zeronebot.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
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
@Log4j2
@RequiredArgsConstructor
public class LoggingAspect {
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
            "(target(ru.skillbox.socialnet.zeronebot.handler.auth.LogoutHandler) || " +
            "(!target(ru.skillbox.socialnet.zeronebot.handler.common.StartCommandHandler) && " +
            "!within(ru.skillbox.socialnet.zeronebot.handler.auth..*)))")
    public Object aroundExceptionHandleAdvice(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        String method = signature.getName();
        String declaringType = signature.getDeclaringTypeName();

        SessionRq sessionRq = (SessionRq) joinPoint.getArgs()[0];
        Long chatId = sessionRq.getChatId();

        InlineKeyboardMarkup markupInLine = keyboardService.buildAuthMenu();

        try {
            tokenService.getToken(sessionRq.getUserSession().getId());
            return joinPoint.proceed();

        } catch (Throwable ex) {
            if (ex instanceof IdException || ex instanceof TokenException) {
                telegramService.sendMessage(
                        chatId,
                        "Вы не авторизованы",
                        markupInLine);
            } else if (ex instanceof BadRequestException badRequestException) {
                if (sessionRq.getRegisterSession().getRegisterState() != null) {
                    telegramService.sendMessage(
                            chatId,
                            badRequestException.getErrorRs().getErrorDescription(),
                            markupInLine);
                } else {
                    telegramService.sendMessage(chatId, badRequestException.getErrorRs().getErrorDescription());
                }
            } else if (ex instanceof IllegalFilterException || ex instanceof OutOfListException) {
                telegramService.sendMessage(chatId, ex.getMessage());
            } else {
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
            }

            String message = ExceptionUtils.getMessage(ex);
            if (message.isEmpty()) {
                message = ExceptionUtils.getRootCauseMessage(ex);
            }

            log.error("Исключение в {}.{}(chatId={}): {}", declaringType, method, chatId, message);
        }

        return null;
    }

    @AfterThrowing(
            pointcut = "execution(* ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler.handle(" +
                    "ru.skillbox.socialnet.zeronebot.dto.request.SessionRq)) && " +
                    "within(ru.skillbox.socialnet.zeronebot.handler.auth..*)",
            throwing = "ex")
    public void afterThrowingHandlerAuthAdvice(JoinPoint joinPoint, Exception ex) {
        Signature signature = joinPoint.getSignature();

        SessionRq sessionRq = (SessionRq) joinPoint.getArgs()[0];
        Long chatId = sessionRq.getChatId();

        String method = signature.getName();
        String declaringType = signature.getDeclaringTypeName();

        if (ex instanceof BadRequestException badRequestException) {
            InlineKeyboardMarkup markupInLine = keyboardService.buildAuthMenu();

            if (sessionRq.getRegisterSession().getRegisterState() != null) {
                telegramService.sendMessage(
                        chatId,
                        "Ошибка регистрации: " + badRequestException.getErrorRs().getErrorDescription(),
                        markupInLine);
            } else {
                telegramService.sendMessage(
                        chatId,
                        "Ошибка авторизации: " + badRequestException.getErrorRs().getErrorDescription(),
                        markupInLine);
            }

            loginSessionService.deleteSession(chatId);
            registerSessionService.deleteSession(chatId);

        } else {
            telegramService.sendMessage(chatId, "Возникла ошибка. Пожалуйста, попробуйте позже");
            loginSessionService.deleteSession(chatId);
            registerSessionService.deleteSession(chatId);
        }

        String message = ExceptionUtils.getMessage(ex);
        if (message.isEmpty()) {
            message = ExceptionUtils.getRootCauseMessage(ex);
        }

        log.error("Исключение в {}.{}(chatId={}): {}", declaringType, method, chatId, message);
    }
}
