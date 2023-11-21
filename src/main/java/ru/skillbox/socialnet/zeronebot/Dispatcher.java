package ru.skillbox.socialnet.zeronebot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.zeronebot.dto.request.SessionRq;
import ru.skillbox.socialnet.zeronebot.handler.UserRequestHandler;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Component
public class Dispatcher {
    private final List<UserRequestHandler> handlers;

    @Autowired
    public Dispatcher(List<UserRequestHandler> handlers) {
        this.handlers = handlers.stream()
                .sorted(Comparator.comparing(UserRequestHandler::isGlobal).reversed())
                .toList();
    }

    public void dispatch(SessionRq sessionRq) throws IOException {
        for (UserRequestHandler userRequestHandler : handlers) {
            if (userRequestHandler.isApplicable(sessionRq)) {
                userRequestHandler.handle(sessionRq);
                break;
            }
        }
    }
}
