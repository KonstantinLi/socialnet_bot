package ru.skillbox.socialnet.zeronebot.handler.friendship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.MessageService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Person.BLOCK;
import static ru.skillbox.socialnet.zeronebot.constant.Person.UNBLOCK;

@Component
@RequiredArgsConstructor
public class BlockUserHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final MessageService messageService;
    private final TelegramService telegramService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isCallbackStartsWith(request.getUpdate(), BLOCK.getCommand()) ||
                isCallbackStartsWith(request.getUpdate(), UNBLOCK.getCommand());
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        boolean blocking = isCallbackStartsWith(request.getUpdate(), BLOCK.getCommand());

        Long id = messageService.getIdFromCallback(
                request,
                blocking ? BLOCK.getCommand() : UNBLOCK.getCommand());
        httpService.blockUser(request, id);

        telegramService.sendMessage(
                request.getChatId(),
                String.format("Вы <b>%s</b> пользователя", blocking ? "заблокировали" : "разблокировали"));
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
