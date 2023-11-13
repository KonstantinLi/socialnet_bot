package ru.skillbox.socialnet.zeronebot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.exception.*;
import ru.skillbox.socialnet.zeronebot.service.KeyboardService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;

@Component
@RequiredArgsConstructor
public class ExceptionHandler {
    private final TelegramService telegramService;
    private final KeyboardService keyboardService;

    public void handle(UserRq request, Exception ex) {
        InlineKeyboardMarkup markupInLine = keyboardService.buildAuthMenu();

        if (ex instanceof BadRequestException) {
            if (request.getRegisterSession().getRegisterState() != null) {
                telegramService.sendMessage(request.getChatId(),
                        ((BadRequestException) ex).getErrorRs().getErrorDescription(),
                        markupInLine);
            } else {
                telegramService.sendMessage(request.getChatId(),
                        ((BadRequestException) ex).getErrorRs().getErrorDescription());
            }

        } else if (ex instanceof IdException || ex instanceof TokenException) {
            telegramService.sendMessage(request.getChatId(),
                    "Вы не авторизованы",
                    markupInLine);

        } else if (ex instanceof IllegalFilterException || ex instanceof OutOfListException) {
            telegramService.sendMessage(request.getChatId(), ex.getMessage());

        } else {
            telegramService.sendMessage(request.getChatId(),
                    "Возникла ошибка. Пожалуйста, попробуйте позже");
        }
    }
}
