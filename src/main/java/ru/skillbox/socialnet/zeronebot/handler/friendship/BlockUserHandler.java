package ru.skillbox.socialnet.zeronebot.handler.friendship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;
import ru.skillbox.socialnet.zeronebot.service.HttpService;
import ru.skillbox.socialnet.zeronebot.service.TelegramService;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Person.BLOCK;
import static ru.skillbox.socialnet.zeronebot.constant.Person.UNBLOCK;

@Component
@RequiredArgsConstructor
public class BlockUserHandler extends UserRequestHandler {
    private final HttpService httpService;
    private final TelegramService telegramService;

    @Override
    public boolean isApplicable(UserRq request) {
        return isCallbackStartsWith(request.getUpdate(), BLOCK) ||
                isCallbackStartsWith(request.getUpdate(), UNBLOCK);
    }

    @Override
    public void handle(UserRq request) throws IOException {
        boolean blocking = isCallbackStartsWith(request.getUpdate(), BLOCK);

        Long id = Long.valueOf(request.getUpdate()
                .getCallbackQuery()
                .getData()
                .replace((blocking ? BLOCK : UNBLOCK) + "_", ""));

        httpService.blockUser(request, id);

        telegramService.sendMessage(
                request.getChatId(),
                String.format("Вы %s пользователя", blocking ? "заблокировали" : "разблокировали"));
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
