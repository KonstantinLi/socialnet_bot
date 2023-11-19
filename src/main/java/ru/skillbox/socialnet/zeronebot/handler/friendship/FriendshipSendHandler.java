package ru.skillbox.socialnet.zeronebot.handler.friendship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.MessageService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Person.ADD;

@Component
@RequiredArgsConstructor
public class FriendshipSendHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final MessageService messageService;
    private final TelegramService telegramService;

    @Override
    public boolean isApplicable(UserRq request) {
        return isCallbackStartsWith(request.getUpdate(), ADD);
    }

    @Override
    public void handle(UserRq request) throws IOException {
        Long id = messageService.getIdFromCallback(request, ADD);
        httpService.sendFriendship(request, id);

        telegramService.sendMessage(
                request.getChatId(),
                "Вы <b>отправили</b> заявку в друзья"
        );
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
