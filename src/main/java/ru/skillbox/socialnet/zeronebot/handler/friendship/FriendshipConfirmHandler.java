package ru.skillbox.socialnet.zeronebot.handler.friendship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Person.CONFIRM;

@Component
@RequiredArgsConstructor
public class FriendshipConfirmHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final TelegramService telegramService;

    @Override
    public boolean isApplicable(UserRq request) {
        return isCallbackStartsWith(request.getUpdate(), CONFIRM);
    }

    @Override
    public void handle(UserRq request) throws IOException {
        Long id = Long.valueOf(request.getUpdate()
                .getCallbackQuery()
                .getData()
                .replace(CONFIRM + "_", ""));

        httpService.addFriend(request, id);

        telegramService.sendMessage(
                request.getChatId(),
                "Вы приняли заявку в друзья"
        );
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
