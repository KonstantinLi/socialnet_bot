package ru.skillbox.socialnet.zeronebot.handler.friendship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.MessageService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Person.DELETE;

@Component
@RequiredArgsConstructor
public class FriendshipDeleteHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final MessageService messageService;
    private final TelegramService telegramService;

    @Override
    public boolean isApplicable(SessionRq request) {
        return isCallbackStartsWith(request.getUpdate(), DELETE.getCommand());
    }

    @Override
    public void handle(SessionRq request) throws Exception {
        Long id = messageService.getIdFromCallback(request, DELETE.getCommand());
        httpService.deleteFriend(request, id);

        telegramService.sendMessage(
                request.getChatId(),
                "Вы <b>удалили</b> пользователя из друзей");
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
