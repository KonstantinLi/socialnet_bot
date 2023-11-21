package ru.skillbox.socialnet.zeronebot.handler.friendship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.MessageService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Person.DECLINE;

@Component
@RequiredArgsConstructor
public class FriendshipDeclineHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final MessageService messageService;
    private final TelegramService telegramService;

    @Override
    public boolean isApplicable(UserRq request) {
        return isCallbackStartsWith(request.getUpdate(), DECLINE.getCommand());
    }

    @Override
    public void handle(UserRq request) throws IOException {
        Long id = messageService.getIdFromCallback(request, DECLINE.getCommand());
        httpService.declineFriendship(request, id);

        telegramService.sendMessage(
                request.getChatId(),
                "Вы <b>отклонили</b> заявку в друзья"
        );
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
