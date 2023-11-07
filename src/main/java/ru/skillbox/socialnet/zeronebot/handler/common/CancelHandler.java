package ru.skillbox.socialnet.zeronebot.handler.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.request.UserRq;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;

import java.io.IOException;

import static ru.skillbox.socialnet.zeronebot.constant.Common.CANCEL;

@Component
@RequiredArgsConstructor
public class CancelHandler extends UserRequestHandler {
    @Override
    public boolean isApplicable(UserRq request) {
        return isTextMessage(request.getUpdate(), CANCEL);
    }

    @Override
    public void handle(UserRq request) throws IOException {
        throw new IOException();
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
