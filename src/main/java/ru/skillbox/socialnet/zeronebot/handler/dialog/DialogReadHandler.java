package ru.skillbox.socialnet.zeronebot.handler.dialog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.MessageService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Dialog.READ;

@Component
@RequiredArgsConstructor
public class DialogReadHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final MessageService messageService;
    private final TelegramService telegramService;

    @Override
    public boolean isApplicable(UserRq request) {
        return isCallbackStartsWith(request.getUpdate(), READ.getCommand());
    }

    @Override
    public void handle(UserRq request) throws IOException {
        Long id = messageService.getIdFromCallback(request, READ.getCommand());
        httpService.readDialog(request, id);

        telegramService.sendMessage(
                request.getChatId(),
                "Вы отметили диалог как <b>прочитанный</b>");
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
