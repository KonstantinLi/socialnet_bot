package ru.skillbox.socialnet.zeronebot.handler.friendship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Person.DELETE;

@Component
@RequiredArgsConstructor
public class FriendshipDeleteHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final TelegramService telegramService;

    @Override
    public boolean isApplicable(UserRq request) {
        return isCallbackStartsWith(request.getUpdate(), DELETE);
    }

    @Override
    public void handle(UserRq request) throws IOException {
        Long id = Long.valueOf(request.getUpdate()
                .getCallbackQuery()
                .getData()
                .replace(DELETE + "_", ""));

        httpService.deleteFriend(request, id);

        telegramService.sendMessage(
                request.getChatId(),
                "Вы удалили пользователя из друзей"
        );
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
